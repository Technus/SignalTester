package com.github.technus.signalTester.test;

import com.github.technus.dbAdditions.functionalInterfaces.ITimedModification;
import com.github.technus.dbAdditions.mongoDB.pojo.UserNT;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.time.Instant;

public class TestResult implements ITimedModification {
    @BsonId
    private ObjectId id;
    private UserNT userNT;
    private Instant timestamp;

    public TestResult() {
        userNT=new UserNT();
        timestamp=Instant.now();
    }

    @BsonCreator
    public TestResult(@BsonId ObjectId id,
                      @BsonProperty("userNT") UserNT userNT,
                      @BsonProperty("timestamp") Instant timestamp) {
        this.id = id;
        this.userNT=userNT;
        this.timestamp=timestamp;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public UserNT getUserNT() {
        return userNT;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
