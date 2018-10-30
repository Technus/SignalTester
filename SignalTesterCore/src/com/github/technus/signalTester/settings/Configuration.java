package com.github.technus.signalTester.settings;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;

@BsonDiscriminator
public class Configuration {
    @BsonId
    private String id="defaultConfiguration";

    public Configuration() {
    }

    @BsonCreator
    public Configuration(@BsonId String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
