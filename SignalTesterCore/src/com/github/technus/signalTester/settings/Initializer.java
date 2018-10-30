package com.github.technus.signalTester.settings;

import com.github.technus.dbAdditions.mongoDB.pojo.ConnectionConfiguration;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;

@BsonDiscriminator
public class Initializer {
    @BsonId
    public String id="defaultInitializer";
    public String localFilesPath=".";
    public ConnectionConfiguration local=new ConnectionConfiguration("localhost","tecAppsLocal");
    public ConnectionConfiguration remote=new ConnectionConfiguration("localhost","tecAppsRemote");
    public String configurationName="defaultConfiguration";
}
