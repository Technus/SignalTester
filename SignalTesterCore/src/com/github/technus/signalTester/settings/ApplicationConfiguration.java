package com.github.technus.signalTester.settings;

import com.github.technus.dbAdditions.functionalInterfaces.ITimedModification;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.Instant;

public class ApplicationConfiguration implements ITimedModification {
    @BsonId
    private final String id;
    private final Instant timestamp;

    public ApplicationConfiguration() {
        id="defaultConfiguration";
        timestamp= Instant.now();
    }

    @BsonCreator
    public ApplicationConfiguration(@BsonId String id,
                                    @BsonProperty("timestamp") Instant timestamp) {
        this.id = id;
        this.timestamp=timestamp;
    }

    public String getId() {
        return id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
