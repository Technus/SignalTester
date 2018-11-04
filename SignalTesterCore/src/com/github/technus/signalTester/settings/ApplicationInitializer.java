package com.github.technus.signalTester.settings;

import com.github.technus.dbAdditions.functionalInterfaces.ITimedModification;
import com.github.technus.dbAdditions.mongoDB.pojo.ConnectionConfiguration;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.Instant;

public class ApplicationInitializer implements ITimedModification {
    @BsonId
    private final String id;
    private final String localFilesPath;
    private final ConnectionConfiguration remote;
    private final String configurationName;
    private final String definitionName;
    private final String languageTag;
    private final Instant timestamp;

    public ApplicationInitializer(){
        this(false);
    }

    public ApplicationInitializer(boolean usingOldTimestamp){
        id="defaultInitializer";
        localFilesPath=".";
        remote=new ConnectionConfiguration("localhost","tecAppsRemote");
        configurationName="defaultConfiguration";
        definitionName="defaultDefinition";
        languageTag ="en-US";
        timestamp=usingOldTimestamp?Instant.ofEpochMilli(0):Instant.now();
    }

    @BsonCreator
    public ApplicationInitializer(
            @BsonId String id,
            @BsonProperty("localFilesPath") String localFilesPath,
            @BsonProperty("remote") ConnectionConfiguration remote,
            @BsonProperty("configurationName") String configurationName,
            @BsonProperty("definitionName") String definitionName,
            @BsonProperty("languageTag") String languageTag,
            @BsonProperty("timestamp") Instant timestamp) {
        this.id = id;
        this.localFilesPath = localFilesPath;
        this.remote = remote;
        this.configurationName = configurationName;
        this.definitionName = definitionName;
        this.languageTag = languageTag;
        this.timestamp=timestamp;
    }

    public String getId() {
        return id;
    }

    public String getLocalFilesPath() {
        return localFilesPath;
    }

    public ConnectionConfiguration getRemote() {
        return remote;
    }

    public String getConfigurationName() {
        return configurationName;
    }

    public String getDefinitionName() {
        return definitionName;
    }

    public String getLanguageTag() {
        return languageTag;
    }

    @Override
    public Instant getTimestamp() {
        return null;
    }
}
