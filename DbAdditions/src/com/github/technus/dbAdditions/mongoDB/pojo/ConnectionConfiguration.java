package com.github.technus.dbAdditions.mongoDB.pojo;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@BsonDiscriminator
public class ConnectionConfiguration {
    private final String address;
    private final int port;
    private final String userName;
    private final String password;
    private final String authenticationDatabase;
    private final String database;

    public ConnectionConfiguration() {
        this("localhost",27017,null,null,null,"admin");
    }
    public ConnectionConfiguration(String address, String database) {
        this(address,27017,null,null,null,database);
    }

    @BsonCreator
    public ConnectionConfiguration(
            @BsonProperty("address") String address,
            @BsonProperty("port") int port,
            @BsonProperty("userName") String userName,
            @BsonProperty("password") String password,
            @BsonProperty("authenticationDatabase") String authenticationDatabase,
            @BsonProperty("database") String database) {
        this.address = address;
        this.port = port;
        this.userName = userName;
        this.password = password;
        this.authenticationDatabase = authenticationDatabase;
        this.database = database;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getAuthenticationDatabase() {
        return authenticationDatabase;
    }

    public String getDatabase() {
        return database;
    }
}
