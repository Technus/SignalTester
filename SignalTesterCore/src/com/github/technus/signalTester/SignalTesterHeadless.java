package com.github.technus.signalTester;

import com.github.technus.dbAdditions.functionalInterfaces.ITimedModification;
import com.github.technus.dbAdditions.mongoDB.MongoClientHandler;
import com.github.technus.dbAdditions.mongoDB.SafePOJO;
import com.github.technus.dbAdditions.mongoDB.fsBackend.FileSystemCollection;
import com.github.technus.dbAdditions.mongoDB.pojo.ConnectionConfiguration;
import com.github.technus.dbAdditions.mongoDB.pojo.ThrowableLog;
import com.github.technus.dbAdditions.mongoDB.pojo.UserNT;
import com.github.technus.dbAdditions.utility.Container;
import com.github.technus.dbAdditions.utility.IContainer;
import com.github.technus.signalTester.settings.ApplicationConfiguration;
import com.github.technus.signalTester.settings.ApplicationInitializer;
import com.github.technus.signalTester.test.TestDefinition;
import com.github.technus.signalTester.test.TestResult;
import com.mongodb.MongoNamespace;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.github.technus.dbAdditions.mongoDB.pojo.ThrowableLog.THROWABLE_LOG_COLLECTION_CODECS;

public class SignalTesterHeadless implements AutoCloseable{
    private String[] args;
    private MongoClientHandler remoteClient;
    private Consumer<Throwable> throwableConsumer;
    private Consumer<TestResult> resultConsumer;
    private MongoCollection<ThrowableLog> throwableCollectionLocal;
    private MongoCollection<ThrowableLog> throwableCollectionRemote;
    private MongoCollection<ApplicationConfiguration> configurationCollectionRemote;
    private MongoCollection<TestDefinition> definitionCollectionRemote;
    private MongoCollection<TestResult> resultCollectionLocal;
    private MongoCollection<TestResult> resultCollectionRemote;

    private IContainer<ApplicationConfiguration> configurationContainer;
    private IContainer<TestDefinition> definitionContainer;

    public static void main(String... args) {
        SignalTesterHeadless signalTesterHeadless=new SignalTesterHeadless(args);

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            signalTesterHeadless.logError(e);
            if(!(e instanceof Exception)){
                System.exit(1);
            }
        });
        Thread.currentThread().setUncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler());

        signalTesterHeadless.initialize();
    }

    public SignalTesterHeadless(List<String> parameters){
        this(parameters!=null?parameters.toArray(new String[0]):new String[0]);
    }

    @SuppressWarnings("unchecked")
    public SignalTesterHeadless(String... args) {
        ThrowableLog.currentApplicationName ="SignalTester";
        throwableConsumer = throwable -> {
            if (throwableCollectionLocal == null) {
                throwable.printStackTrace();
            } else {
                try {
                    boolean failed = false;
                    ThrowableLog log;
                    try {
                        log = new ThrowableLog(throwable);
                    } catch (Exception e) {
                        new Exception("Unable to create full throwable log", e).printStackTrace();
                        log = new ThrowableLog(throwable, 10);
                        failed = true;
                    }
                    try {
                        throwableCollectionLocal.insertOne(log);
                    } catch (Exception e) {
                        new Exception("Unable to insert throwable log", e).printStackTrace();
                        failed = true;
                    }
                    if (failed) {
                        throwable.printStackTrace();
                    }
                } catch (Throwable t) {
                    new Error("Failed to log error", t).printStackTrace();
                }
            }
        };

        resultConsumer=result -> {
            if(resultCollectionLocal==null){
                throw new Error("TestResult collection cannot be null");
            }else {
                try {
                    resultCollectionLocal.insertOne(result);
                }catch (Exception e){
                    throw new Error("Unable to insert result log",e);
                }
            }
        };
        this.args=args;
    }

    public void initialize() {
        CodecRegistry initializerRegistry = SafePOJO.buildCodecRegistryWithOtherClassesOrCodecs(
                ApplicationInitializer.class, ApplicationInitializer.class, ConnectionConfiguration.class);
        CodecRegistry configurationCodecs = SafePOJO.buildCodecRegistryWithOtherClassesOrCodecs(
                ApplicationConfiguration.class, ApplicationConfiguration.class, ApplicationConfiguration.class);
        CodecRegistry definitionCodecs = SafePOJO.buildCodecRegistryWithOtherClassesOrCodecs(
                TestDefinition.class, TestDefinition.class);
        CodecRegistry resultCodecs = SafePOJO.buildCodecRegistryWithOtherClassesOrCodecs(
                TestResult.class, TestResult.class, UserNT.class);

        ApplicationInitializer applicationInitializer;
        File initializerFile;
        if (args != null && args.length > 0 && args[0] != null && args[0].endsWith('.' + FileSystemCollection.EXTENSION)) {
            initializerFile = new File(args[0]);
        }else{
            initializerFile = new File("defaultInitializer." + FileSystemCollection.EXTENSION).getAbsoluteFile();
        }

        if (initializerFile.isFile()) {
            try {
                applicationInitializer = SafePOJO.decode(BsonDocument.parse(new String(
                        Files.readAllBytes(initializerFile.toPath()))), ApplicationInitializer.class, initializerRegistry);
            }catch (IOException e){
                throw new Error("Unable to read initializer "+initializerFile.getAbsolutePath(),e);
            }catch (Exception e){
                throw new Error("Unable to decode initializer",e);
            }
        } else {
            applicationInitializer = new ApplicationInitializer();
            BsonDocument initializerDocument = SafePOJO.encode(applicationInitializer, ApplicationInitializer.class, initializerRegistry);
            try {
                Files.write(initializerFile.toPath(), initializerDocument.toJson(JsonWriterSettings.builder().indent(true).build()).getBytes());
            }catch (IOException e){
                logError(new Exception("Couldn't write initializer to "+initializerFile.getAbsolutePath(),e));
            }catch (Exception e){
                logError(new Exception("Couldn't encode initializer",e));
            }
        }

        try {
            Locale.setDefault(Locale.forLanguageTag(applicationInitializer.getLanguageTag()));
        } catch (Exception e) {
            logError(new Exception("Couldn't set locale, using default en_US",e));
            Locale.setDefault(Locale.US);
        }

        throwableCollectionLocal = new FileSystemCollection<>(new File(applicationInitializer.getLocalFilesPath()),
                new MongoNamespace("tecAppsLocal", ThrowableLog.class.getSimpleName()), ThrowableLog.class)
                .withCodecRegistry(THROWABLE_LOG_COLLECTION_CODECS);
        resultCollectionLocal = new FileSystemCollection<>(new File(applicationInitializer.getLocalFilesPath()),
                new MongoNamespace("tecAppsLocal", TestResult.class.getSimpleName()), TestResult.class)
                .withCodecRegistry(resultCodecs);

        remoteClient = new MongoClientHandler(applicationInitializer.getRemote(),
                commandFailedEvent -> logError(commandFailedEvent.getThrowable()), () -> {
            if (resultCollectionLocal != null && resultCollectionRemote != null) {
                for (TestResult result : resultCollectionLocal.find()) {
                    ObjectId id = result.getId();
                    try {
                        result.setId(null);
                        resultCollectionRemote.insertOne(result);
                    } catch (Exception e) {
                        logError(new Exception("Couldn't insert result to remote",e));
                        break;
                    }finally {
                        result.setId(id);
                    }
                    try{
                        resultCollectionLocal.deleteOne(new Document().append("_id", result.getId()));
                    }catch (Exception e){
                        logError(new Exception("Couldn't delete result from local",e));
                        break;
                    }
                }
            }
            if (throwableCollectionLocal != null && throwableCollectionRemote != null) {
                for (ThrowableLog log : throwableCollectionLocal.find()) {
                    ObjectId id = log.getId();
                    try {
                        log.setId(null);
                        throwableCollectionRemote.insertOne(log);
                        throwableCollectionLocal.deleteOne(new Document().append("_id", id));
                    } catch (Exception e) {
                        new Exception("Couldn't insert throwable log to remote",e).printStackTrace();//just to be sure that there is no infinite work to do...
                        break;
                    }
                }
            }
        });
        MongoDatabase remoteDatabase = remoteClient.getDatabase();

        try {
            remoteDatabase.runCommand(new Document().append("ping", ""));
        } catch (MongoTimeoutException e) {
            e.printStackTrace();
        } catch (Exception e) {
            logError(new Exception("Couldn't ping database",e));
        }

        throwableCollectionRemote = remoteDatabase.getCollection(ThrowableLog.class.getSimpleName(), ThrowableLog.class)
                .withCodecRegistry(THROWABLE_LOG_COLLECTION_CODECS);
        configurationCollectionRemote = remoteDatabase.getCollection(ApplicationConfiguration.class.getSimpleName(), ApplicationConfiguration.class)
                .withCodecRegistry(configurationCodecs);
        definitionCollectionRemote = remoteDatabase.getCollection(TestDefinition.class.getSimpleName(), TestDefinition.class)
                .withCodecRegistry(definitionCodecs);
        resultCollectionRemote = remoteDatabase.getCollection(TestResult.class.getSimpleName(), TestResult.class)
                .withCodecRegistry(resultCodecs);

        File configurationFile = new File(applicationInitializer.getLocalFilesPath() + File.separator + applicationInitializer.getConfigurationName() + '.' + FileSystemCollection.EXTENSION);
        try {
            setContainerContent(ApplicationConfiguration.class, ApplicationConfiguration::new, configurationContainer = new Container<>(), configurationFile, applicationInitializer.getConfigurationName(), configurationCollectionRemote);
        }catch (IOException e){
            logError(new Exception("Couldn't save configuration locally",e));
        }catch (Exception e){
            logError(new Exception("Couldn't encode configuration",e));
        }
        File definitionFile = new File(applicationInitializer.getLocalFilesPath() + File.separator + applicationInitializer.getDefinitionName() + '.' + FileSystemCollection.EXTENSION);
        try{
            setContainerContent(TestDefinition.class,TestDefinition::new,definitionContainer=new Container<>(),definitionFile,applicationInitializer.getDefinitionName(),definitionCollectionRemote);
        }catch (IOException e){
            logError(new Exception("Couldn't save definition locally",e));
        }catch (Exception e){
            logError(new Exception("Couldn't encode definition",e));
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends ITimedModification> void setContainerContent(Class<T> clazz, Supplier<T> newInstanceCreator, IContainer<T> container, File redundantFile, String redundantName, MongoCollection<T> collection) throws Exception {
        T remote = null, local;
        try {
            remote= collection.find(new Document().append("_id", redundantName)).first();
        } catch (MongoTimeoutException e) {
            e.printStackTrace();
        } catch (Exception e) {
            logError(new Exception("Couldn't get remote object, using default",e));
        } finally {
            if (remote == null) {
                remote = newInstanceCreator.get();
            }
        }
        if (redundantFile.isFile()) {
            local = SafePOJO.decode(BsonDocument.parse(new String(Files.readAllBytes(redundantFile.toPath()))), clazz, collection.getCodecRegistry());
        } else {
            local = remote;
        }
        container.accept(ITimedModification.getNewest(remote, local));
        if (redundantFile.isFile() || !redundantFile.exists()) {
            Files.write(redundantFile.toPath(), SafePOJO.encode(
                    container.get(), clazz, collection.getCodecRegistry()).toJson(JsonWriterSettings.builder().indent(true).build()).getBytes());
        }
        container.get();
    }

    @Override
    public void close() {
        remoteClient.close();
    }

    public MongoCollection<ApplicationConfiguration> getConfigurationCollectionRemote() {
        return configurationCollectionRemote;
    }

    public MongoCollection<TestDefinition> getDefinitionCollectionRemote() {
        return definitionCollectionRemote;
    }

    public IContainer<ApplicationConfiguration> getConfigurationContainer() {
        return configurationContainer;
    }

    public IContainer<TestDefinition> getDefinitionContainer() {
        return definitionContainer;
    }

    public void logError(Throwable t) {
        if (throwableConsumer == null) {
            t.printStackTrace();
        } else {
            throwableConsumer.accept(t);
        }
    }

    public void logResult(TestResult result){
        if (resultConsumer == null) {
            resultCollectionLocal.insertOne(result);
        } else {
            resultConsumer.accept(result);
        }
    }

    public MongoCollection<TestResult> getResultCollectionRemote() {
        return resultCollectionRemote;
    }

    public Consumer<Throwable> getThrowableConsumer() {
        return throwableConsumer;
    }

    public void setThrowableConsumer(Consumer<Throwable> throwableConsumer) {
        this.throwableConsumer = throwableConsumer;
    }

    public Consumer<TestResult> getResultConsumer() {
        return resultConsumer;
    }

    public void setResultConsumer(Consumer<TestResult> resultConsumer) {
        this.resultConsumer = resultConsumer;
    }
}
