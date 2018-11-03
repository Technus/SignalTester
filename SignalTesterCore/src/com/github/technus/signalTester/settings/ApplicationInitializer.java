package com.github.technus.signalTester.settings;

import com.github.technus.dbAdditions.mongoDB.pojo.ConnectionConfiguration;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class ApplicationInitializer {
    @BsonId
    private final String id;
    private final String localFilesPath;
    private final ConnectionConfiguration remote;
    private final String configurationName;
    private final String definitionName;
    private final String languageTag;

    public ApplicationInitializer(){
        id="defaultInitializer";
        localFilesPath=".";
        remote=new ConnectionConfiguration("localhost","tecAppsRemote");
        configurationName="defaultConfiguration";
        definitionName="defaultDefinition";
        languageTag ="en-US";
    }

    @BsonCreator
    public ApplicationInitializer(
            @BsonId String id,
            @BsonProperty("localFilesPath") String localFilesPath,
            @BsonProperty("remote") ConnectionConfiguration remote,
            @BsonProperty("configurationName") String configurationName,
            @BsonProperty("definitionName") String definitionName,
            @BsonProperty("languageTag") String languageTag) {
        this.id = id;
        this.localFilesPath = localFilesPath;
        this.remote = remote;
        this.configurationName = configurationName;
        this.definitionName = definitionName;
        this.languageTag = languageTag;
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
}
