package com.github.technus.signalTester;

import com.github.technus.dbAdditions.mongoDB.MongoClientHandler;
import com.github.technus.dbAdditions.mongoDB.SafePOJO;
import com.github.technus.dbAdditions.mongoDB.fsBackend.FileSystemCollection;
import com.github.technus.dbAdditions.mongoDB.pojo.ConnectionConfiguration;
import com.github.technus.dbAdditions.mongoDB.pojo.ThrowableLog;
import com.github.technus.signalTester.settings.Configuration;
import com.github.technus.signalTester.settings.Initializer;
import com.mongodb.MongoNamespace;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sun.security.auth.module.NTSystem;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.Locale;
import java.util.function.Consumer;

import static com.github.technus.dbAdditions.mongoDB.pojo.ThrowableLog.THROWABLE_LOG_COLLECTION_CODECS;

public class SignalTesterHeadless implements AutoCloseable{
    private final MongoClientHandler localClient, remoteClient;
    private MongoCollection<ThrowableLog> throwableLogCollectionLocal;
    private MongoCollection<ThrowableLog> throwableLogCollectionRemote;
    private MongoCollection<Configuration> configurationCollectionLocal;
    private MongoCollection<Configuration> configurationCollectionRemote;
    private Consumer<Throwable> throwableConsumer;

    public SignalTesterHeadless(String... args) {
        ThrowableLog.currentApplicationName ="SignalTester";

        throwableConsumer = throwable -> {
            if (getThrowableLogCollectionLocal() == null) {
                throwable.printStackTrace();
            } else {
                ThrowableLog log;
                try {
                    log = new ThrowableLog(throwable);
                }catch (Exception e){
                    new Exception("Unable to create full throwable log",e).printStackTrace();
                    log = new ThrowableLog(throwable,10);
                }
                try {
                    getThrowableLogCollectionLocal().insertOne(log);
                }catch (Exception e){
                    new Exception("Unable to insert throwable log",e).printStackTrace();
                }
            }
        };

        Initializer initializer;
        File initializerFile=new File("defaultInitializer."+FileSystemCollection.EXTENSION).getAbsoluteFile();
        if(args!=null && args.length>0 && args[0]!=null && args[0].endsWith('.'+FileSystemCollection.EXTENSION)) {
            initializerFile=new File(args[0]);
        }
        CodecRegistry initializerRegistry = SafePOJO.buildCodecRegistryWithOtherClasses(
                Initializer.class,Initializer.class,Initializer.class,ConnectionConfiguration.class);
        if (initializerFile.isFile()) {
            try {
                initializer = SafePOJO.decode(BsonDocument.parse(new String(
                        Files.readAllBytes(initializerFile.toPath()))),Initializer.class,initializerRegistry);
            }catch (IOException e){
                throw new IOError(e);
            }catch (Exception e){
                throw new Error("Cannot initialize with "+initializerFile.getAbsolutePath(),e);
            }
        } else {
            initializer=new Initializer();
            BsonDocument initializerDocument= SafePOJO.encode(initializer,Initializer.class,initializerRegistry);
            try {
                Files.write(initializerFile.toPath(),
                        initializerDocument.toJson(JsonWriterSettings.builder().indent(true).build()).getBytes());
            }catch (IOException e){
                throw new IOError(e);
            }
        }

        CodecRegistry configurationCollectionCodecs=
                SafePOJO.buildCodecRegistry(Configuration.class,Configuration.class,Configuration.class);//discriminate

        localClient =new MongoClientHandler(initializer.local,commandFailedEvent -> {
            System.out.println("COMMAND FAILED "+commandFailedEvent.getCommandName());
        },()->{

        });
        remoteClient =new MongoClientHandler(initializer.remote, commandFailedEvent -> {
            System.out.println("COMMAND FAILED "+commandFailedEvent.getCommandName());
        },()->{
            if(getThrowableLogCollectionLocal()!=null && getThrowableLogCollectionRemote()!=null) {
                for (ThrowableLog log : getThrowableLogCollectionLocal().find()) {
                    ObjectId id=log.getId();
                    try {
                        log.setId(null);
                        getThrowableLogCollectionRemote().insertOne(log);
                        getThrowableLogCollectionLocal().deleteOne(new Document().append("_id",id));
                    }catch (Exception e){
                        e.printStackTrace();//just to be sure that there is no infinite work to do...
                        return;
                    }
                }
            }
            if(getConfigurationCollectionLocal()!=null && getConfigurationCollectionRemote()!=null){
                for (Configuration configuration : getConfigurationCollectionLocal().find()) {
                    String id=configuration.getId();
                    try {
                        //configuration.setId(null);
                        getConfigurationCollectionRemote().insertOne(configuration);
                        getConfigurationCollectionLocal().deleteOne(new Document().append("_id",id));
                    }catch (Exception e){
                        logError(e);
                        return;
                    }
                }
            }
        });
        MongoDatabase localDatabase = localClient.getDatabase();
        MongoDatabase remoteDatabase = remoteClient.getDatabase();

        MongoCollection<ThrowableLog> throwableLogCollectionFileSystem = new FileSystemCollection<>(new File(initializer.localFilesPath),
                new MongoNamespace(initializer.local.getDatabase(), ThrowableLog.class.getSimpleName()), ThrowableLog.class)
                .withCodecRegistry(THROWABLE_LOG_COLLECTION_CODECS);

        MongoCollection<Configuration> configurationCollectionFileSystem = new FileSystemCollection<>(new File(initializer.localFilesPath),
                new MongoNamespace(initializer.local.getDatabase(), Configuration.class.getSimpleName()), Configuration.class)
                .withCodecRegistry(configurationCollectionCodecs);

        throwableLogCollectionLocal= localDatabase.getCollection(ThrowableLog.class.getSimpleName(),ThrowableLog.class)
                .withCodecRegistry(THROWABLE_LOG_COLLECTION_CODECS);

        configurationCollectionLocal= localDatabase.getCollection(Configuration.class.getSimpleName(),Configuration.class)
                .withCodecRegistry(configurationCollectionCodecs);

        try {
            localDatabase.runCommand(new Document().append("ping", ""));
            for(ThrowableLog log: throwableLogCollectionFileSystem.find()){
                throwableLogCollectionLocal.insertOne(log);
            }
            throwableLogCollectionFileSystem.drop();
            throwableLogCollectionFileSystem =null;
        }catch (MongoTimeoutException e){
            throwableLogCollectionLocal= throwableLogCollectionFileSystem;
            //logError(e);
            e.printStackTrace();
        }

        try {
            localDatabase.runCommand(new Document().append("ping", ""));
            for(Configuration configuration: configurationCollectionFileSystem.find()){
                configurationCollectionLocal.insertOne(configuration);
            }
            configurationCollectionFileSystem.drop();
            configurationCollectionFileSystem =null;
        }catch (MongoTimeoutException e){
            configurationCollectionLocal= configurationCollectionFileSystem;
            //logError(e);
            e.printStackTrace();
        }

        try {
            remoteDatabase.runCommand(new Document().append("ping", ""));
        }catch (MongoTimeoutException e){
            //logError(e);
            e.printStackTrace();
        }finally{
            throwableLogCollectionRemote= remoteDatabase.getCollection(ThrowableLog.class.getSimpleName(),ThrowableLog.class)
                    .withCodecRegistry(THROWABLE_LOG_COLLECTION_CODECS);
            configurationCollectionRemote= remoteDatabase.getCollection(Configuration.class.getSimpleName(),Configuration.class)
                    .withCodecRegistry(configurationCollectionCodecs);
        }
        if(configurationCollectionLocal.estimatedDocumentCount()==0) {
            configurationCollectionLocal.insertOne(new Configuration());
        }
    }

    @Override
    public void close() {
        localClient.close();
        remoteClient.close();
    }

    public MongoCollection<ThrowableLog> getThrowableLogCollectionLocal() {
        return throwableLogCollectionLocal;
    }

    public MongoCollection<ThrowableLog> getThrowableLogCollectionRemote() {
        return throwableLogCollectionRemote;
    }

    public MongoCollection<Configuration> getConfigurationCollectionLocal() {
        return configurationCollectionLocal;
    }

    public MongoCollection<Configuration> getConfigurationCollectionRemote() {
        return configurationCollectionRemote;
    }

    public void logError(Throwable t) {
        if (throwableConsumer != null) {
            try {
                throwableConsumer.accept(t);
            } catch (Throwable error) {
                Throwable T = new Throwable("Unable to consume throwable! " + t.getClass().getName() + ": " + t.getMessage(), error);
                T.setStackTrace(t.getStackTrace());
                throwableConsumer.accept(T);
            }
        }
    }

    private static NTSystem ntSystem=new NTSystem();
    public static String getUserName(){
        return ntSystem.getDomain()+"\\"+ntSystem.getName();
    }

    public static String getSystemName(){
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Throwable e) {
            return null;
        }
    }

    public static String getFullName(){
        return getSystemName()+"\\"+getUserName();
    }

    public static void main(String... args) {
        Locale.setDefault(Locale.US);
        try{
            new SignalTesterHeadless(args);
        }catch (Throwable t){
            t.printStackTrace();
            System.exit(1);
        }
    }
}
