package com.github.technus.signalTester.test;

import com.github.technus.dbAdditions.functionalInterfaces.ITimedModification;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.Instant;

public class TestDefinition implements ITimedModification {
    @BsonId
    private final String id;
    private final Instant timestamp;

    public TestDefinition() {
        id="defaultDefinition";
        timestamp=Instant.now();
    }

    @BsonCreator
    public TestDefinition(@BsonId String id,
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
