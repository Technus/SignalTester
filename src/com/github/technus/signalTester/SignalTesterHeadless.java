package com.github.technus.signalTester;

import com.github.technus.dbAdditions.mongoDB.MongoClientHandler;
import com.github.technus.dbAdditions.mongoDB.SafePOJO;
import com.github.technus.dbAdditions.mongoDB.pojo.ThrowableLog;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sun.security.auth.module.NTSystem;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;

import java.net.InetAddress;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;

import static com.github.technus.dbAdditions.mongoDB.pojo.ThrowableLog.THROWABLE_LOG_COLLECTION_CODECS;

public class SignalTesterHeadless implements AutoCloseable{
    private final MongoClientHandler localClient, remoteClient;
    private final MongoCollection<ThrowableLog> throwableLogCollectionLocal,throwableLogCollectionRemote;
    private final MongoCollection<Configuration> configurationCollectionLocal,configurationCollectionRemote;
    private Consumer<Throwable> throwableConsumer;

    public SignalTesterHeadless() {
        ThrowableLog.currentApplicationName ="SignalTester";

        throwableConsumer = throwable -> {
            ThrowableLog log;
            try {
                log = new ThrowableLog(throwable);
            }catch (Exception e){
                log = new ThrowableLog(throwable,10);
            }
            if(getThrowableLogCollectionLocal()!=null) {
                try {
                    getThrowableLogCollectionLocal().insertOne(log);
                    return;
                }catch (Exception ignored){}
            }

            //todo filebackup?
        };

        CodecRegistry configurationCollectionCodecs=
                SafePOJO.buildCodecRegistry(Configuration.class,Configuration.class,Configuration.class);//discriminate

        localClient =new MongoClientHandler("localhost",27017,"tecAppsLocal",commandFailedEvent -> {
            System.out.println(commandFailedEvent.getCommandName());
        },()->{

        });
        remoteClient =new MongoClientHandler("localhost",27017,"tecAppsRemote", commandFailedEvent -> {
            System.out.println(commandFailedEvent.getCommandName());
        },()->{
            if(getThrowableLogCollectionLocal()!=null && getThrowableLogCollectionRemote()!=null) {
                for (ThrowableLog log : getThrowableLogCollectionLocal().find()) {
                    ObjectId id=log.getId();
                    try {
                        log.setId(null);
                        getThrowableLogCollectionRemote().insertOne(log);
                        getThrowableLogCollectionLocal().deleteOne(new Document().append("_id",id));
                    }catch (Exception e){

                        return;
                    }
                }
            }
        });
        MongoDatabase localDatabase = localClient.getDatabase();
        MongoDatabase remoteDatabase = remoteClient.getDatabase();

        try {
            localDatabase.runCommand(new Document().append("ping", ""));
        }catch (MongoTimeoutException e){
            logError(e);
        }
        try {
            remoteDatabase.runCommand(new Document().append("ping", ""));
        }catch (MongoTimeoutException e){
            logError(e);
        }

        throwableLogCollectionLocal= localDatabase.getCollection(ThrowableLog.class.getSimpleName(),ThrowableLog.class)
                .withCodecRegistry(THROWABLE_LOG_COLLECTION_CODECS);
        throwableLogCollectionRemote= remoteDatabase.getCollection(ThrowableLog.class.getSimpleName(),ThrowableLog.class)
                .withCodecRegistry(THROWABLE_LOG_COLLECTION_CODECS);
        configurationCollectionLocal= localDatabase.getCollection(Configuration.class.getSimpleName(),Configuration.class)
                .withCodecRegistry(configurationCollectionCodecs);
        configurationCollectionRemote= remoteDatabase.getCollection(Configuration.class.getSimpleName(),Configuration.class)
                .withCodecRegistry(configurationCollectionCodecs);
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

            } catch (Error error) {
                Error e = new Error("Unable to consume throwable! " + t.getClass().getName() + ": " + t.getMessage(), error);
                e.setStackTrace(t.getStackTrace());
                throwableConsumer.accept(e);
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
            new SignalTesterHeadless();
        }catch (Throwable t){
            t.printStackTrace();
            System.exit(1);
        }
    }
}
