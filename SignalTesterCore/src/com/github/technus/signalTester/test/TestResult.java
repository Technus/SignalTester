package com.github.technus.signalTester.test;

import com.github.technus.dbAdditions.functionalInterfaces.ITimedModification;
import com.github.technus.dbAdditions.mongoDB.pojo.SystemUser;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.time.Instant;

public class TestResult implements ITimedModification {
    @BsonId
    private ObjectId id;
    private SystemUser systemUser;
    private Instant timestamp;

    public TestResult() {
        systemUser =new SystemUser();
        timestamp=Instant.now();
    }

    @BsonCreator
    public TestResult(@BsonId ObjectId id,
                      @BsonProperty("systemUser") SystemUser systemUser,
                      @BsonProperty("timestamp") Instant timestamp) {
        this.id = id;
        this.systemUser = systemUser;
        this.timestamp=timestamp;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public SystemUser getSystemUser() {
        return systemUser;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
