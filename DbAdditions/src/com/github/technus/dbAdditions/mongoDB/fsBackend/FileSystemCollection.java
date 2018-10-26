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
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.FileFilter;
import java.io.IOError;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

import static com.mongodb.assertions.Assertions.notNull;
import static com.mongodb.client.model.ReplaceOptions.createReplaceOptions;

public class FileSystemCollection<TDocument> implements MongoCollection<TDocument> {
    public static final String EXTENSION =".json";
    public static final FileFilter FILE_FILTER = file -> file.isFile() && file.getPath().endsWith(EXTENSION);

    private final File serverFolder;
    private final IContainer<File> collectionFolder;

    private final Indexes indexes;

    private final IContainer<MongoNamespace> namespace;
    private final Class<TDocument> documentClass;
    private final CodecRegistry codecRegistry;
    private final WriteConcern writeConcern;
    private final boolean retryWrites;


    public FileSystemCollection(final File serverFolder, final MongoNamespace namespace, final Class<TDocument> documentClass) {
        this(serverFolder,namespace,documentClass, MongoClient.getDefaultCodecRegistry(),WriteConcern.ACKNOWLEDGED,true);
    }

    @SuppressWarnings("unchecked")
    public FileSystemCollection(final File serverFolder, final MongoNamespace namespace, final Class<TDocument> documentClass,
                                 final CodecRegistry codecRegistry, final WriteConcern writeConcern,
                                 final boolean retryWrites) {
        try {
            this.serverFolder = notNull("serverFolder",serverFolder).getAbsoluteFile();
            this.namespace = IContainer.DEFAULT_IMPLEMENTATION.newInstance();
            this.namespace.accept(notNull("namespace", namespace));
            this.collectionFolder = IContainer.DEFAULT_IMPLEMENTATION.newInstance();
            this.collectionFolder.accept(new File(serverFolder.getAbsolutePath() + File.separator + namespace.getDatabaseName() + File.separator + namespace.getCollectionName()));
        }catch (InstantiationException|IllegalAccessException e){
            throw new RuntimeException(e);
        }
        this.documentClass = notNull("documentClass", documentClass);
        this.codecRegistry = notNull("codecRegistry", codecRegistry);
        this.writeConcern = notNull("writeConcern", writeConcern);
        this.retryWrites = retryWrites;
        this.indexes=new Indexes();
        init();
    }

    private FileSystemCollection(File serverFolder, IContainer<File> collectionFolder,
                                 IContainer<MongoNamespace> namespace, Class<TDocument> documentClass,
                                 CodecRegistry codecRegistry, WriteConcern writeConcern, boolean retryWrites,
                                 Indexes indexes) {
        this.serverFolder = serverFolder;
        this.collectionFolder = collectionFolder;
        this.namespace = namespace;
        this.documentClass = documentClass;
        this.codecRegistry = codecRegistry;
        this.writeConcern = writeConcern;
        this.retryWrites = retryWrites;
        this.indexes=indexes;
    }

    private void init(){
        validateFolder(serverFolder);
        validateFolder(collectionFolder.get());
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
        return collectionFolder.get();
    }

    @Override
    public MongoNamespace getNamespace() {
        return namespace.get();
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
        return new FileSystemCollection<>(serverFolder,collectionFolder,namespace, documentClass, codecRegistry, writeConcern, retryWrites,indexes);
    }

    @Override
    public MongoCollection<TDocument> withCodecRegistry(final CodecRegistry codecRegistry) {
        return new FileSystemCollection<>(serverFolder,collectionFolder,namespace, documentClass, codecRegistry, writeConcern, retryWrites,indexes);
    }

    @Override
    public MongoCollection<TDocument> withReadPreference(final ReadPreference readPreference) {
        return this;
    }

    @Override
    public MongoCollection<TDocument> withWriteConcern(final WriteConcern writeConcern) {
        return new FileSystemCollection<>(serverFolder,collectionFolder,namespace, documentClass, codecRegistry, writeConcern, retryWrites,indexes);
    }

    @Override
    public MongoCollection<TDocument> withReadConcern(final ReadConcern readConcern) {
        return this;
    }

    private boolean matches(File file, BsonDocument document,BsonDocument filter){

    }

    private ArrayList<String> filter(BsonDocument filter){
        ArrayList<String> list=new ArrayList<>();
        File[] files=collectionFolder.get().listFiles(FILE_FILTER);
        if(files!=null){
            for(File file:files){
                try {
                    BsonDocument document = BsonDocument.parse(new String(Files.readAllBytes(file.toPath())));
                    if(matches(file,document,filter)){
                        list.add(file.getName());
                    }
                }catch (IOException e){
                    throw new IOError(e);
                }
            }
        }
        return list;
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
        File[] files=collectionFolder.get().listFiles(FILE_FILTER);
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
        throw new NoSuchMethodError();
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
    public void insertOne(ClientSession clientSession, TDocument t, InsertOneOptions options) {
        BsonDocumentWriter bsonWriter=new BsonDocumentWriter(new BsonDocument());
        codecRegistry.get(documentClass).encode(bsonWriter,t, EncoderContext.builder().isEncodingCollectibleDocument(true).build());
                    //todo check if gets codec correctly!
        bsonWriter.flush();
        BsonDocument document=bsonWriter.getDocument();
        BsonValue bsonValue=document.get("_id");
        String id=null;
        switch (bsonValue.getBsonType()){
            case STRING:
                id=bsonValue.asString().getValue();
                break;
            case OBJECT_ID:
                id=bsonValue.asObjectId().getValue().toHexString();
                break;
            case NULL: case UNDEFINED:
                ObjectId objectId=new ObjectId(Date.from(Instant.now()));
                id=objectId.toHexString();
                document.put("_id",new BsonObjectId(objectId));
                break;
            default:
                throw new RuntimeException("_id must be a ObjectID or String that matches a valid filename");
        }
        try{
            File docFile=new File(collectionFolder.get().getAbsolutePath()+File.separator+id+EXTENSION);
            Files.write(docFile.toPath(),document.toString().getBytes());
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
        throw new NoSuchMethodError();
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
        throw new NoSuchMethodError();
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
        throw new NoSuchMethodError();
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
        return updateMany(clientSession,filter,update);
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
        return findOneAndUpdate(clientSession,filter,update);
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
            Files.deleteIfExists(collectionFolder.get().toPath());
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

    private class Index<K>{
        private final String name;
        private final Function<BsonDocument,K> keyGenerator;
        private final TreeMap<K,ArrayList<String>> indexMap;
        private final HashMap<String,K> fileMap;

        private Index(String name, Comparator<K> comparator, Function<BsonDocument,K> keyGenerator) {
            this.name = notNull("name",name);
            this.indexMap = new TreeMap<>(notNull("comparator",comparator));
            this.keyGenerator = notNull("keyGenerator",keyGenerator);
            this.fileMap = new HashMap<>();
        }

        private void clear(){
            indexMap.clear();
            fileMap.clear();
        }

        private void buildIndex(){
            clear();
            File[] files=FileSystemCollection.this.collectionFolder.get().listFiles(FILE_FILTER);
            if(files!=null) {
                for (File file :files) {
                    try{
                        BsonDocument document = BsonDocument.parse(new String(Files.readAllBytes(file.toPath())));
                        build(file,document);
                    }catch (IOException e){
                        throw new IOError(e);
                    }
                }
            }
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

    private class Indexes {
        private final HashMap<String,Index> indexMap =new HashMap<>();

        private void remove(String indexName){
            indexMap.remove(indexName);
        }

        private void put(Index index){
            indexMap.put(index.name,index);
        }

        private void clear(){
            indexMap.clear();
        }

        private void buildAllIndexes(){
            indexMap.forEach((s, index) -> index.clear());
            File[] files=FileSystemCollection.this.collectionFolder.get().listFiles(FILE_FILTER);
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
        File newCollectionFolder=new File(serverFolder.getAbsolutePath() + File.separator + newCollectionNamespace.getDatabaseName() + File.separator + newCollectionNamespace.getCollectionName());
        validateFolder(newCollectionFolder);
        try {
            Files.move(collectionFolder.get().toPath(), newCollectionFolder.toPath());
            namespace.accept(newCollectionNamespace);
            collectionFolder.accept(newCollectionFolder);
        }catch (IOException e){
            throw new IOError(e);
        }
    }
}
