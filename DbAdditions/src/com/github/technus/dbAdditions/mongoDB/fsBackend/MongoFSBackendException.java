package com.github.technus.dbAdditions.mongoDB.fsBackend;

import com.mongodb.MongoException;

public class MongoFSBackendException extends MongoException {
    public MongoFSBackendException(String msg, Throwable t) {
        super(msg, t);
    }
}
