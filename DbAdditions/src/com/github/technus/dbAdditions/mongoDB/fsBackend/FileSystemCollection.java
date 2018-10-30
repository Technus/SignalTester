package com.github.technus.dbAdditions.mongoDB.fsBackend;

import com.github.technus.dbAdditions.utility.IContainer;
import com.mongodb.*;
import com.mongodb.MongoClient;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.*;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.FileFilter;
import java.io.IOError;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.mongodb.assertions.Assertions.notNull;
import static com.mongodb.client.model.ReplaceOptions.createReplaceOptions;
import static org.bson.BsonType.UNDEFINED;

public class FileSystemCollection<TDocument> implements MongoCollection<TDocument> {
    public static final String EXTENSION ="json";
    public static final FileFilter FILE_FILTER = file -> file.isFile() && file.getPath().endsWith('.'+EXTENSION);

    private final File serverFolder;
    private final IContainer<File> collectionFolderContainer;

    private final Indexes indexes;

    private final IContainer<MongoNamespace> namespaceContainer;
    private final Class<TDocument> documentClass;
    private final CodecRegistry codecRegistry;
    private final WriteConcern writeConcern;


    public FileSystemCollection(final File serverFolder, final MongoNamespace namespace, final Class<TDocument> documentClass) {
        this(serverFolder,namespace,documentClass, MongoClient.getDefaultCodecRegistry(),WriteConcern.ACKNOWLEDGED);
    }

    @SuppressWarnings("unchecked")
    public FileSystemCollection(final File serverFolder, final MongoNamespace namespace, final Class<TDocument> documentClass,
                                 final CodecRegistry codecRegistry, final WriteConcern writeConcern) {
        try {
            this.serverFolder = notNull("serverFolder",serverFolder).getAbsoluteFile();
            this.namespaceContainer = IContainer.DEFAULT_IMPLEMENTATION.newInstance();
            this.namespaceContainer.accept(notNull("namespaceContainer", namespace));
            this.collectionFolderContainer = IContainer.DEFAULT_IMPLEMENTATION.newInstance();
            this.collectionFolderContainer.accept(new File(serverFolder.getAbsolutePath() + File.separator + namespace.getDatabaseName() + File.separator + namespace.getCollectionName()));
        }catch (InstantiationException|IllegalAccessException e){
            throw new RuntimeException(e);
        }
        this.documentClass = notNull("documentClass", documentClass);
        this.codecRegistry = notNull("codecRegistry", codecRegistry);
        this.writeConcern = notNull("writeConcern", writeConcern);
        this.indexes=new Indexes();
        init();
    }

    private FileSystemCollection(File serverFolder, IContainer<File> collectionFolderContainer,
                                 IContainer<MongoNamespace> namespaceContainer, Class<TDocument> documentClass,
                                 CodecRegistry codecRegistry, WriteConcern writeConcern, Indexes indexes) {
        this.serverFolder = serverFolder;
        this.collectionFolderContainer = collectionFolderContainer;
        this.namespaceContainer = namespaceContainer;
        this.documentClass = documentClass;
        this.codecRegistry = codecRegistry;
        this.writeConcern = writeConcern;
        this.indexes=indexes;
    }

    private void init(){
        validateFolder(serverFolder);
        validateFolder(collectionFolderContainer.get());
        indexes.buildAllIndexes(collectionFolderContainer.get());
    }

    private void validateFolder(File folder){
        folder=folder.getAbsoluteFile();
        if(!folder.exists()){
            if(!folder.mkdirs()){
                throw new IOError(new IOException("Unable to create directories: "+folder.getPath()));
            }
        }
        if(!folder.exists()){
                throw new IOError(new IOException("Root still does not exist: "+folder.getPath()));
        }
        if(!folder.isDirectory()){
            throw new IOError(new IOException("Root is not directory: "+folder.getPath()));
        }
        if(!folder.canWrite()){
            throw new IOError(new IOException("Root is not writable: "+folder.getPath()));
        }
        if(!folder.canRead()){
            throw new IOError(new IOException("Root is not readable: "+folder.getPath()));
        }
    }

    public File getServerFolder() {
        return serverFolder;
    }

    public File getCollectionFolder() {
        return collectionFolderContainer.get();
    }

    @Override
    public MongoNamespace getNamespace() {
        return namespaceContainer.get();
    }

    @Override
    public Class<TDocument> getDocumentClass() {
        return documentClass;
    }

    @Override
    public CodecRegistry getCodecRegistry() {
        return codecRegistry;
    }

    @Override
    public ReadPreference getReadPreference() {
        return ReadPreference.primary();
    }

    @Override
    public WriteConcern getWriteConcern() {
        return writeConcern;
    }

    @Override
    public ReadConcern getReadConcern() {
        return ReadConcern.LOCAL;
    }

    @Override
    public <NewTDocument> MongoCollection<NewTDocument> withDocumentClass(final Class<NewTDocument> documentClass) {
        return new FileSystemCollection<>(serverFolder, collectionFolderContainer, namespaceContainer, documentClass, codecRegistry, writeConcern, indexes);
    }

    @Override
    public MongoCollection<TDocument> withCodecRegistry(final CodecRegistry codecRegistry) {
        return new FileSystemCollection<>(serverFolder, collectionFolderContainer, namespaceContainer, documentClass, codecRegistry, writeConcern, indexes);
    }

    @Override
    public MongoCollection<TDocument> withReadPreference(final ReadPreference readPreference) {
        return this;
    }

    @Override
    public MongoCollection<TDocument> withWriteConcern(final WriteConcern writeConcern) {
        return new FileSystemCollection<>(serverFolder, collectionFolderContainer, namespaceContainer, documentClass, codecRegistry, writeConcern, indexes);
    }

    @Override
    public MongoCollection<TDocument> withReadConcern(final ReadConcern readConcern) {
        return this;
    }

    @Override
    @Deprecated
    public long count() {
        return countDocuments();
    }

    @Override
    @Deprecated
    public long count(Bson filter) {
        return countDocuments(filter);
    }

    @Override
    @Deprecated
    public long count(Bson filter, CountOptions options) {
        return countDocuments(filter,options);
    }

    @Override
    @Deprecated
    public long count(ClientSession clientSession) {
        return countDocuments(clientSession);
    }

    @Override
    @Deprecated
    public long count(ClientSession clientSession, Bson filter) {
        return countDocuments(clientSession,filter);
    }

    @Override
    @Deprecated
    public long count(ClientSession clientSession, Bson filter, CountOptions options) {
        return countDocuments(clientSession,filter,options);
    }

    @Override
    public long countDocuments() {
        return countDocuments(null,new BsonDocument(),new CountOptions());
    }

    @Override
    public long countDocuments(Bson filter) {
        return countDocuments(null,filter,new CountOptions());
    }

    @Override
    public long countDocuments(Bson filter, CountOptions options) {
        return countDocuments(null,filter,options);
    }

    @Override
    public long countDocuments(ClientSession clientSession) {
        return countDocuments(clientSession,new BsonDocument(),new CountOptions());
    }

    @Override
    public long countDocuments(ClientSession clientSession, Bson filter) {
        return countDocuments(clientSession,filter,new CountOptions());
    }

    @Override
    public long countDocuments(ClientSession clientSession, Bson filter, CountOptions options) {
        throw new NoSuchMethodError();
    }

    @Override
    public long estimatedDocumentCount() {
        return estimatedDocumentCount(new EstimatedDocumentCountOptions());
    }

    @Override
    public long estimatedDocumentCount(EstimatedDocumentCountOptions options) {
        File[] files=collectionFolderContainer.get().listFiles(FILE_FILTER);
        return files==null?0:files.length;
    }

    @Override
    public <TResult> DistinctIterable<TResult> distinct(String fieldName, Class<TResult> tResultClass) {
        return distinct(null,fieldName,new BsonDocument(),tResultClass);
    }

    @Override
    public <TResult> DistinctIterable<TResult> distinct(String fieldName, Bson filter, Class<TResult> tResultClass) {
        return distinct(null,fieldName,filter,tResultClass);
    }

    @Override
    public <TResult> DistinctIterable<TResult> distinct(ClientSession clientSession, String fieldName, Class<TResult> tResultClass) {
        return distinct(clientSession,fieldName,new BsonDocument(),tResultClass);
    }

    @Override
    public <TResult> DistinctIterable<TResult> distinct(ClientSession clientSession, String fieldName, Bson filter, Class<TResult> tResultClass) {
        throw new NoSuchMethodError();
    }

    @Override
    public FindIterable<TDocument> find() {
        return find(null,new BsonDocument(),documentClass);
    }

    @Override
    public <TResult> FindIterable<TResult> find(Class<TResult> tResultClass) {
        return find(null,new BsonDocument(),tResultClass);
    }

    @Override
    public FindIterable<TDocument> find(Bson filter) {
        return find(null,filter,documentClass);
    }

    @Override
    public <TResult> FindIterable<TResult> find(Bson filter, Class<TResult> tResultClass) {
        return find(null,filter,tResultClass);
    }

    @Override
    public FindIterable<TDocument> find(ClientSession clientSession) {
        return find(clientSession,new BsonDocument(),documentClass);
    }

    @Override
    public <TResult> FindIterable<TResult> find(ClientSession clientSession, Class<TResult> tResultClass) {
        return find(clientSession,new BsonDocument(),tResultClass);
    }

    @Override
    public FindIterable<TDocument> find(ClientSession clientSession, Bson filter) {
        return find(clientSession,filter,documentClass);
    }

    @Override
    public <TResult> FindIterable<TResult> find(ClientSession clientSession, Bson filter, Class<TResult> tResultClass) {
        BsonValue value=null;
        if (filter != null) {
            BsonDocument document=filter.toBsonDocument(Document.class,codecRegistry);
            value=document.get("_id");
        }
        String idQuery;
        switch (value==null?UNDEFINED:value.getBsonType()){
            case STRING:
                idQuery=value.asString().getValue();
                break;
            case OBJECT_ID:
                idQuery=value.asObjectId().getValue().toHexString();
                break;
            case NULL: case UNDEFINED: default:
                idQuery=null;
        }
        List<TResult> list=new ArrayList<>();
        for(String fileName: lookupFileNamesById(idQuery)){
            try {
                File file=new File(collectionFolderContainer.get().getAbsolutePath()+File.separator+fileName);
                BsonDocument fileDocument = BsonDocument.parse(new String(Files.readAllBytes(file.toPath())));
                TResult object=codecRegistry.get(tResultClass).decode(new BsonDocumentReader(fileDocument), DecoderContext.builder().checkedDiscriminator(false).build());
                list.add(object);
            }catch (IOException e){
                throw new IOError(e);
            }
        }
        return new SimpleFindIterable<TResult>() {
            @Override
            public MongoCursor<TResult> iterator() {
                return new MongoCursor<TResult>() {
                    private int position=0;

                    @Override
                    public void close() {
                        position=list.size();
                    }

                    @Override
                    public boolean hasNext() {
                        return position<list.size();
                    }

                    @Override
                    public TResult next() {
                        return list.get(position++);
                    }

                    @Override
                    public TResult tryNext() {
                        try{
                            return next();
                        }catch (IndexOutOfBoundsException e){
                            return null;
                        }
                    }

                    @Override
                    public ServerCursor getServerCursor() {
                        return null;
                    }

                    @Override
                    public ServerAddress getServerAddress() {
                        return null;
                    }
                };
            }

            @Override
            public TResult first() {
                return list.get(0);
            }

            @Override
            public <U> MongoIterable<U> map(com.mongodb.Function<TResult, U> mapper) {
                return null;
            }

            @Override
            public void forEach(Block<? super TResult> block) {
                list.forEach(block::apply);
            }

            @Override
            public <A extends Collection<? super TResult>> A into(A target) {
                return null;
            }
        };
    }
    
    private interface SimpleFindIterable<TResult> extends FindIterable<TResult>{
        @Override
        default FindIterable<TResult> filter(Bson filter) {
            return this;
        }

        @Override
        default FindIterable<TResult> limit(int limit) {
            return this;
        }

        @Override
        default FindIterable<TResult> skip(int skip) {
            return this;
        }

        @Override
        default FindIterable<TResult> maxTime(long maxTime, TimeUnit timeUnit) {
            return this;
        }

        @Override
        default FindIterable<TResult> maxAwaitTime(long maxAwaitTime, TimeUnit timeUnit) {
            return this;
        }

        @Override
        @Deprecated
        default FindIterable<TResult> modifiers(Bson modifiers) {
            return this;
        }

        @Override
        default FindIterable<TResult> projection(Bson projection) {
            return this;
        }

        @Override
        default FindIterable<TResult> sort(Bson sort) {
            return this;
        }

        @Override
        default FindIterable<TResult> noCursorTimeout(boolean noCursorTimeout) {
            return this;
        }

        @Override
        default FindIterable<TResult> oplogReplay(boolean oplogReplay) {
            return this;
        }

        @Override
        default FindIterable<TResult> partial(boolean partial) {
            return this;
        }

        @Override
        default FindIterable<TResult> cursorType(CursorType cursorType) {
            return this;
        }

        @Override
        default FindIterable<TResult> batchSize(int batchSize) {
            return this;
        }

        @Override
        default FindIterable<TResult> collation(Collation collation) {
            return this;
        }

        @Override
        default FindIterable<TResult> comment(String comment) {
            return this;
        }

        @Override
        default FindIterable<TResult> hint(Bson hint) {
            return this;
        }

        @Override
        default FindIterable<TResult> max(Bson max) {
            return this;
        }

        @Override
        default FindIterable<TResult> min(Bson min) {
            return this;
        }

        @Override
        @Deprecated
        default FindIterable<TResult> maxScan(long maxScan) {
            return this;
        }

        @Override
        default FindIterable<TResult> returnKey(boolean returnKey) {
            return this;
        }

        @Override
        default FindIterable<TResult> showRecordId(boolean showRecordId) {
            return this;
        }

        @Override
        @Deprecated
        default FindIterable<TResult> snapshot(boolean snapshot) {
            return this;
        }
    }

    @Override
    public AggregateIterable<TDocument> aggregate(List<? extends Bson> pipeline) {
        return aggregate(null,pipeline,documentClass);
    }

    @Override
    public <TResult> AggregateIterable<TResult> aggregate(List<? extends Bson> pipeline, Class<TResult> tResultClass) {
        return aggregate(null,pipeline,tResultClass);
    }

    @Override
    public AggregateIterable<TDocument> aggregate(ClientSession clientSession, List<? extends Bson> pipeline) {
        return aggregate(clientSession,pipeline,documentClass);
    }

    @Override
    public <TResult> AggregateIterable<TResult> aggregate(ClientSession clientSession, List<? extends Bson> pipeline, Class<TResult> tResultClass) {
        throw new NoSuchMethodError();
    }

    @Override
    public ChangeStreamIterable<TDocument> watch() {
        return watch(null, Collections.emptyList(),documentClass);
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(Class<TResult> tResultClass) {
        return watch(null,Collections.emptyList(),tResultClass);
    }

    @Override
    public ChangeStreamIterable<TDocument> watch(List<? extends Bson> pipeline) {
        return watch(null,pipeline,documentClass);
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(List<? extends Bson> pipeline, Class<TResult> tResultClass) {
        return watch(null,pipeline,tResultClass);
    }

    @Override
    public ChangeStreamIterable<TDocument> watch(ClientSession clientSession) {
        return watch(clientSession,Collections.emptyList(),documentClass);
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(ClientSession clientSession, Class<TResult> tResultClass) {
        return watch(clientSession,Collections.emptyList(),tResultClass);
    }

    @Override
    public ChangeStreamIterable<TDocument> watch(ClientSession clientSession, List<? extends Bson> pipeline) {
        return watch(clientSession,pipeline,documentClass);
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(ClientSession clientSession, List<? extends Bson> pipeline, Class<TResult> tResultClass) {
        throw new NoSuchMethodError();
    }

    @Override
    public MapReduceIterable<TDocument> mapReduce(String mapFunction, String reduceFunction) {
        return mapReduce(null,mapFunction,reduceFunction,documentClass);
    }

    @Override
    public <TResult> MapReduceIterable<TResult> mapReduce(String mapFunction, String reduceFunction, Class<TResult> tResultClass) {
        return mapReduce(null,mapFunction,reduceFunction,tResultClass);
    }

    @Override
    public MapReduceIterable<TDocument> mapReduce(ClientSession clientSession, String mapFunction, String reduceFunction) {
        return mapReduce(clientSession,mapFunction,reduceFunction,documentClass);
    }

    @Override
    public <TResult> MapReduceIterable<TResult> mapReduce(ClientSession clientSession, String mapFunction, String reduceFunction, Class<TResult> tResultClass) {
        throw new NoSuchMethodError();
    }

    @Override
    public BulkWriteResult bulkWrite(List<? extends WriteModel<? extends TDocument>> requests) {
        return bulkWrite(null,Collections.emptyList(),new BulkWriteOptions());
    }

    @Override
    public BulkWriteResult bulkWrite(List<? extends WriteModel<? extends TDocument>> requests, BulkWriteOptions options) {
        return bulkWrite(null,requests,options);
    }

    @Override
    public BulkWriteResult bulkWrite(ClientSession clientSession, List<? extends WriteModel<? extends TDocument>> requests) {
        return bulkWrite(clientSession,requests,new BulkWriteOptions());
    }

    @Override
    public BulkWriteResult bulkWrite(ClientSession clientSession, List<? extends WriteModel<? extends TDocument>> requests, BulkWriteOptions options) {
        throw new NoSuchMethodError();
    }

    @Override
    public void insertOne(TDocument t) {
        insertOne(null,t,new InsertOneOptions());
    }

    @Override
    public void insertOne(TDocument t, InsertOneOptions options) {
        insertOne(null,t,options);
    }

    @Override
    public void insertOne(ClientSession clientSession, TDocument t) {
        insertOne(clientSession,t,new InsertOneOptions());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void insertOne(ClientSession clientSession, TDocument t, InsertOneOptions options) {
        BsonDocumentWriter bsonWriter=new BsonDocumentWriter(new BsonDocument());
        Codec codec=codecRegistry.get(t.getClass());
        if(codec==null){
            codec=codecRegistry.get(documentClass);//get default codec
        }
        codec.encode(bsonWriter, t, EncoderContext.builder().isEncodingCollectibleDocument(true).build());
        bsonWriter.flush();
        BsonDocument document=bsonWriter.getDocument();
        BsonValue bsonValue=document.get("_id");
        File docFile;
        switch (bsonValue==null?UNDEFINED:bsonValue.getBsonType()){
            case STRING:
                docFile=new File(collectionFolderContainer.get().getAbsolutePath()+File.separator+
                        bsonValue.asString().getValue()+'.'+EXTENSION);
                break;
            case OBJECT_ID:
                docFile=new File(collectionFolderContainer.get().getAbsolutePath()+File.separator+
                        bsonValue.asObjectId().getValue().toHexString()+'.'+EXTENSION);
                break;
            case NULL: case UNDEFINED:
                do {
                    ObjectId objectId = new ObjectId(Date.from(Instant.now()));
                    docFile=new File(collectionFolderContainer.get().getAbsolutePath()+File.separator+
                            objectId.toHexString()+'.'+EXTENSION);
                    document.put("_id",new BsonObjectId(objectId));
                }while(docFile.exists());
                break;
            default:
                throw new RuntimeException("_id must be a ObjectID or String that matches a valid filename");
        }
        try{
            Files.write(docFile.toPath(),document.toJson(JsonWriterSettings.builder().indent(true).build()).getBytes());
            indexes.updateAllIndexes(docFile,document);
        }catch (IOException e){
            throw new IOError(e);
        }
    }

    @Override
    public void insertMany(List<? extends TDocument> ts) {
        insertMany(null,ts,new InsertManyOptions());
    }

    @Override
    public void insertMany(List<? extends TDocument> ts, InsertManyOptions options) {
        insertMany(null,ts,options);
    }

    @Override
    public void insertMany(ClientSession clientSession, List<? extends TDocument> ts) {
        insertMany(clientSession,ts,new InsertManyOptions());
    }

    @Override
    public void insertMany(ClientSession clientSession, List<? extends TDocument> ts, InsertManyOptions options) {
        ts.forEach((Consumer<TDocument>) this::insertOne);
    }

    @Override
    public DeleteResult deleteOne(Bson filter) {
        return deleteOne(null,filter,new DeleteOptions());
    }

    @Override
    public DeleteResult deleteOne(Bson filter, DeleteOptions options) {
        return deleteOne(null,filter,options);
    }

    @Override
    public DeleteResult deleteOne(ClientSession clientSession, Bson filter) {
        return deleteOne(clientSession,filter,new DeleteOptions());
    }

    @Override
    public DeleteResult deleteOne(ClientSession clientSession, Bson filter, DeleteOptions options) {
        BsonValue value=null;
        if (filter != null) {
            BsonDocument document=filter.toBsonDocument(Document.class,codecRegistry);
            value=document.get("_id");
        }
        String idQuery;
        switch (value==null?UNDEFINED:value.getBsonType()){
            case STRING:
                idQuery=value.asString().getValue();
                break;
            case OBJECT_ID:
                idQuery=value.asObjectId().getValue().toHexString();
                break;
            case NULL: case UNDEFINED: default:
                idQuery=null;
        }
        int deletedCount=0;
        for(String fileName: lookupFileNamesById(idQuery)){
            try {
                File docFile=new File(collectionFolderContainer.get().getAbsolutePath()+File.separator+fileName);
                Files.delete(docFile.toPath());
                indexes.updateAllIndexes(docFile,null);
                deletedCount++;
                break;
            }catch (IOException e){
                throw new IOError(e);
            }
        }
        return DeleteResult.acknowledged(deletedCount);
    }

    public void deleteOne(String filename){
        try{
            File docFile=new File(collectionFolderContainer.get().getAbsolutePath()+File.separator+filename);
            Files.delete(docFile.toPath());
            indexes.updateAllIndexes(docFile,null);
        }catch (IOException e){
            throw new IOError(e);
        }
    }

    @Override
    public DeleteResult deleteMany(Bson filter) {
        return deleteMany(null,filter,new DeleteOptions());
    }

    @Override
    public DeleteResult deleteMany(Bson filter, DeleteOptions options) {
        return deleteMany(null,filter,options);
    }

    @Override
    public DeleteResult deleteMany(ClientSession clientSession, Bson filter) {
        return deleteMany(clientSession,filter,new DeleteOptions());
    }

    @Override
    public DeleteResult deleteMany(ClientSession clientSession, Bson filter, DeleteOptions options) {
        BsonValue value=null;
        if (filter != null) {
            BsonDocument document=filter.toBsonDocument(Document.class,codecRegistry);
            value=document.get("_id");
        }
        String idQuery;
        switch (value==null?UNDEFINED:value.getBsonType()){
            case STRING:
                idQuery=value.asString().getValue();
                break;
            case OBJECT_ID:
                idQuery=value.asObjectId().getValue().toHexString();
                break;
            case NULL: case UNDEFINED: default:
                idQuery=null;
        }
        int deletedCount=0;
        for(String fileName: lookupFileNamesById(idQuery)){
            try {
                File docFile=new File(collectionFolderContainer.get().getAbsolutePath()+File.separator+fileName);
                Files.delete(docFile.toPath());
                deletedCount++;
                indexes.updateAllIndexes(docFile,null);
            }catch (IOException e){
                throw new IOError(e);
            }
        }
        return DeleteResult.acknowledged(deletedCount);
    }

    public void deleteMany(String... fileNames){
        for (String fileName : fileNames) {
            deleteOne(fileName);
        }
    }

    @Override
    public UpdateResult replaceOne(Bson filter, TDocument replacement) {
        return replaceOne(null,filter,replacement,new ReplaceOptions());
    }

    @Override
    @Deprecated
    public UpdateResult replaceOne(Bson filter, TDocument replacement, UpdateOptions updateOptions) {
        return replaceOne(null,filter,replacement,createReplaceOptions(updateOptions));
    }

    @Override
    public UpdateResult replaceOne(Bson filter, TDocument replacement, ReplaceOptions replaceOptions) {
        return replaceOne(null,filter,replacement,replaceOptions);
    }

    @Override
    public UpdateResult replaceOne(ClientSession clientSession, Bson filter, TDocument replacement) {
        return replaceOne(clientSession,filter,replacement,new ReplaceOptions());
    }

    @Override
    @Deprecated
    public UpdateResult replaceOne(ClientSession clientSession, Bson filter, TDocument replacement, UpdateOptions updateOptions) {
        return replaceOne(clientSession,filter,replacement,createReplaceOptions(updateOptions));
    }

    @Override
    public UpdateResult replaceOne(ClientSession clientSession, Bson filter, TDocument replacement, ReplaceOptions replaceOptions) {
        throw new NoSuchMethodError();
    }

    @Override
    public UpdateResult updateOne(Bson filter, Bson update) {
        return updateOne(null,filter,update,new UpdateOptions());
    }

    @Override
    public UpdateResult updateOne(Bson filter, Bson update, UpdateOptions updateOptions) {
        return updateOne(null,filter,update,updateOptions);
    }

    @Override
    public UpdateResult updateOne(ClientSession clientSession, Bson filter, Bson update) {
        return updateOne(clientSession,filter,update,new UpdateOptions());
    }

    @Override
    public UpdateResult updateOne(ClientSession clientSession, Bson filter, Bson update, UpdateOptions updateOptions) {
        throw new NoSuchMethodError();
    }

    @Override
    public UpdateResult updateMany(Bson filter, Bson update) {
        return updateMany(null,filter,update,new UpdateOptions());
    }

    @Override
    public UpdateResult updateMany(Bson filter, Bson update, UpdateOptions updateOptions) {
        return updateMany(null,filter,update,updateOptions);
    }

    @Override
    public UpdateResult updateMany(ClientSession clientSession, Bson filter, Bson update) {
        return updateMany(clientSession,filter,update,new UpdateOptions());
    }

    @Override
    public UpdateResult updateMany(ClientSession clientSession, Bson filter, Bson update, UpdateOptions updateOptions) {
        throw new NoSuchMethodError();
    }

    @Override
    public TDocument findOneAndDelete(Bson filter) {
        return findOneAndDelete(null,filter,new FindOneAndDeleteOptions());
    }

    @Override
    public TDocument findOneAndDelete(Bson filter, FindOneAndDeleteOptions options) {
        return findOneAndDelete(null,filter,options);
    }

    @Override
    public TDocument findOneAndDelete(ClientSession clientSession, Bson filter) {
        return findOneAndDelete(clientSession,filter,new FindOneAndDeleteOptions());
    }

    @Override
    public TDocument findOneAndDelete(ClientSession clientSession, Bson filter, FindOneAndDeleteOptions options) {
        throw new NoSuchMethodError();
    }

    @Override
    public TDocument findOneAndReplace(Bson filter, TDocument replacement) {
        return findOneAndReplace(null,filter,replacement,new FindOneAndReplaceOptions());
    }

    @Override
    public TDocument findOneAndReplace(Bson filter, TDocument replacement, FindOneAndReplaceOptions options) {
        return findOneAndReplace(null,filter,replacement,options);
    }

    @Override
    public TDocument findOneAndReplace(ClientSession clientSession, Bson filter, TDocument replacement) {
        return findOneAndReplace(clientSession,filter,replacement,new FindOneAndReplaceOptions());
    }

    @Override
    public TDocument findOneAndReplace(ClientSession clientSession, Bson filter, TDocument replacement, FindOneAndReplaceOptions options) {
        throw new NoSuchMethodError();
    }

    @Override
    public TDocument findOneAndUpdate(Bson filter, Bson update) {
        return findOneAndUpdate(null,filter,update,new FindOneAndUpdateOptions());
    }

    @Override
    public TDocument findOneAndUpdate(Bson filter, Bson update, FindOneAndUpdateOptions options) {
        return findOneAndUpdate(null,filter,update,options);
    }

    @Override
    public TDocument findOneAndUpdate(ClientSession clientSession, Bson filter, Bson update) {
        return findOneAndUpdate(clientSession,filter,update,new FindOneAndUpdateOptions());
    }

    @Override
    public TDocument findOneAndUpdate(ClientSession clientSession, Bson filter, Bson update, FindOneAndUpdateOptions options) {
        throw new NoSuchMethodError();
    }

    @Override
    public void drop() {
        drop(null);
    }

    @Override
    public void drop(ClientSession clientSession) {
        try {
            Files.deleteIfExists(collectionFolderContainer.get().toPath());
        }catch (IOException e){
            throw new IOError(e);
        }
    }

    @Override
    public String createIndex(Bson keys) {
        return createIndex(null,keys,new IndexOptions());
    }

    @Override
    public String createIndex(Bson keys, IndexOptions indexOptions) {
        return createIndex(null,keys,indexOptions);
    }

    @Override
    public String createIndex(ClientSession clientSession, Bson keys) {
        return createIndex(clientSession,keys,new IndexOptions());
    }

    @Override
    public String createIndex(ClientSession clientSession, Bson keys, IndexOptions indexOptions) {
        throw new NoSuchMethodError();
    }

    @Override
    public List<String> createIndexes(List<IndexModel> indexes) {
        return createIndexes(null,indexes,new CreateIndexOptions());
    }

    @Override
    public List<String> createIndexes(List<IndexModel> indexes, CreateIndexOptions createIndexOptions) {
        return createIndexes(null,indexes,createIndexOptions);
    }

    @Override
    public List<String> createIndexes(ClientSession clientSession, List<IndexModel> indexes) {
        return createIndexes(clientSession,indexes,new CreateIndexOptions());
    }

    @Override
    public List<String> createIndexes(ClientSession clientSession, List<IndexModel> indexes, CreateIndexOptions createIndexOptions) {
        throw new NoSuchMethodError();
    }

    @Override
    public ListIndexesIterable<Document> listIndexes() {
        return listIndexes(null,Document.class);
    }

    @Override
    public <TResult> ListIndexesIterable<TResult> listIndexes(Class<TResult> tResultClass) {
        return listIndexes(null,tResultClass);
    }

    @Override
    public ListIndexesIterable<Document> listIndexes(ClientSession clientSession) {
        return listIndexes(clientSession,Document.class);
    }

    @Override
    public <TResult> ListIndexesIterable<TResult> listIndexes(ClientSession clientSession, Class<TResult> tResultClass) {
        throw new NoSuchMethodError();
    }

    @Override
    public void dropIndex(String indexName) {
        dropIndex(null,indexName,new DropIndexOptions());
    }

    @Override
    public void dropIndex(String indexName, DropIndexOptions dropIndexOptions) {
        dropIndex(null,indexName,dropIndexOptions);
    }

    @Override
    public void dropIndex(Bson keys) {
        dropIndex(null,keys,new DropIndexOptions());
    }

    @Override
    public void dropIndex(Bson keys, DropIndexOptions dropIndexOptions) {
        dropIndex(null,keys,dropIndexOptions);
    }

    @Override
    public void dropIndex(ClientSession clientSession, String indexName) {
        dropIndex(clientSession,indexName,new DropIndexOptions());
    }

    @Override
    public void dropIndex(ClientSession clientSession, Bson keys) {
        dropIndex(clientSession,keys,new DropIndexOptions());
    }

    @Override
    public void dropIndex(ClientSession clientSession, String indexName, DropIndexOptions dropIndexOptions) {
        indexes.remove(indexName);
    }

    @Override
    public void dropIndex(ClientSession clientSession, Bson keys, DropIndexOptions dropIndexOptions) {
        throw new NoSuchMethodError();
    }

    @Override
    public void dropIndexes() {
        dropIndexes(null,new DropIndexOptions());
    }

    @Override
    public void dropIndexes(ClientSession clientSession) {
        dropIndexes(clientSession,new DropIndexOptions());
    }

    @Override
    public void dropIndexes(DropIndexOptions dropIndexOptions) {
        dropIndexes(null,dropIndexOptions);
    }

    @Override
    public void dropIndexes(ClientSession clientSession, DropIndexOptions dropIndexOptions) {
        indexes.clear();
    }

    public static class Index<K>{
        private final String name;
        private final Function<BsonDocument,K> keyGenerator;
        private final TreeMap<K,ArrayList<String>> indexMap;
        private final HashMap<String,K> fileMap;

        public Index(String name, Comparator<K> comparator, Function<BsonDocument,K> keyGenerator) {
            this.name = notNull("name",name);
            this.indexMap = new TreeMap<>(notNull("comparator",comparator));
            this.keyGenerator = notNull("keyGenerator",keyGenerator);
            this.fileMap = new HashMap<>();
        }

        private void clear(){
            indexMap.clear();
            fileMap.clear();
        }

        //private void buildIndex(){
        //    clear();
        //    File[] files=FileSystemCollection.this.collectionFolderContainer.get().listFiles(FILE_FILTER);
        //    if(files!=null) {
        //        for (File file :files) {
        //            try{
        //                BsonDocument document = BsonDocument.parse(new String(Files.readAllBytes(file.toPath())));
        //                build(file,document);
        //            }catch (IOException e){
        //                throw new IOError(e);
        //            }
        //        }
        //    }
        //}

        private List<String> lookupFileNames(K key){
            if(key==null){
                return new ArrayList<>(fileMap.keySet());
            }
            ArrayList<String> fileNames=indexMap.get(key);
            return fileNames==null?new ArrayList<>():new ArrayList<>(fileNames);
        }

        private K reverseLookup(String fileName){
            return fileMap.get(fileName);
        }

        private void build(File file,BsonDocument document){
            K key=keyGenerator.apply(document);
            ArrayList<String> files = indexMap.computeIfAbsent(key, k -> new ArrayList<>());
            files.add(file.getName());
            fileMap.put(file.getName(),key);
        }

        private void update(File file,BsonDocument document){
            K key=fileMap.remove(file.getName());
            if(key!=null){
                ArrayList<String> files=indexMap.get(key);
                if(files!=null) {
                    files.remove(file.getName());
                    if (file.length() == 0) {
                        indexMap.remove(key);
                    }
                }
            }
            if(document!=null){
                build(file,document);
            }
        }
    }

    private static class Indexes {
        private final HashMap<String,Index> indexMap =new HashMap<>();

        private Indexes(){
            put(new Index<String>("_id", Comparator.naturalOrder(), document -> {
                BsonValue value=document.get("_id");
                switch (value==null?UNDEFINED:value.getBsonType()){
                    case STRING:
                        return value.asString().getValue();
                    case OBJECT_ID:
                        return value.asObjectId().getValue().toHexString();
                    case NULL: case UNDEFINED: default:
                        throw new RuntimeException("_id must be a ObjectID or String that matches a valid filename");
                }
            }));
        }

        private void remove(String indexName){
            indexMap.remove(indexName);
        }

        private void put(Index index){
            indexMap.put(index.name,index);
        }

        @SuppressWarnings("unchecked")
        private <K> Index<K> get(String indexName,Class<K> clazz){
            return indexMap.get(indexName);
        }

        private void clear(){
            indexMap.clear();
        }

        private void buildAllIndexes(File collectionFolder){
            indexMap.forEach((s, index) -> index.clear());
            File[] files=collectionFolder.listFiles(FILE_FILTER);
            if(files!=null) {
                for (File file :files) {
                    try {
                        BsonDocument document = BsonDocument.parse(new String(Files.readAllBytes(file.toPath())));
                        indexMap.forEach((s, index) -> index.build(file,document));
                    }catch (IOException e){
                        throw new IOError(e);
                    }
                }
            }
        }

        private void updateAllIndexes(File file,BsonDocument document){
            indexMap.forEach((s, index) -> index.update(file,document));
        }
    }

    private List<String> lookupFileNamesById(String id){
        return indexes.get("_id",String.class).lookupFileNames(id);
    }

    @Override
    public void renameCollection(MongoNamespace newCollectionNamespace) {
        renameCollection(null,newCollectionNamespace,new RenameCollectionOptions());
    }

    @Override
    public void renameCollection(MongoNamespace newCollectionNamespace, RenameCollectionOptions renameCollectionOptions) {
        renameCollection(null,newCollectionNamespace,renameCollectionOptions);
    }

    @Override
    public void renameCollection(ClientSession clientSession, MongoNamespace newCollectionNamespace) {
        renameCollection(clientSession,newCollectionNamespace,new RenameCollectionOptions());
    }

    @Override
    public void renameCollection(ClientSession clientSession, MongoNamespace newCollectionNamespace, RenameCollectionOptions renameCollectionOptions) {
        notNull("newCollectionNamespace",newCollectionNamespace);
        File newCollectionFolder=new File(serverFolder.getAbsolutePath() + File.separator + newCollectionNamespace.getDatabaseName() + File.separator + newCollectionNamespace.getCollectionName());
        validateFolder(newCollectionFolder);
        try {
            Files.move(collectionFolderContainer.get().toPath(), newCollectionFolder.toPath());
            namespaceContainer.accept(newCollectionNamespace);
            collectionFolderContainer.accept(newCollectionFolder);
        }catch (IOException e){
            throw new IOError(e);
        }
    }
}
