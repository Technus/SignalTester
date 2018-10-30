package com.github.technus.dbAdditions.mongoDB;


import com.github.technus.dbAdditions.mongoDB.pojo.ConnectionConfiguration;
import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import com.mongodb.event.*;

public class MongoClientHandler implements AutoCloseable{
    private MongoClient client;
    private MongoDatabase mongoDatabase;
    private byte delay;

    public MongoClientHandler(ConnectionConfiguration connectionConfiguration, ICommandFailRunner commandFailRunner, Runnable connectionValidRunner){
        this(connectionConfiguration.getAddress(), connectionConfiguration.getPort(), connectionConfiguration.getUserName(), connectionConfiguration.getPassword(), connectionConfiguration.getAuthenticationDatabase(), connectionConfiguration.getDatabase(),commandFailRunner,connectionValidRunner);
    }

    public MongoClientHandler(String address, int port, String database, ICommandFailRunner commandFailRunner, Runnable connectionValidRunner){
        this(address,port,null,null,null,database,commandFailRunner,connectionValidRunner);
    }

    public MongoClientHandler(String address, int port, String user, String password, String authenticationDatabase, String database, ICommandFailRunner commandFailRunner, Runnable connectionValidRunner) throws MongoException {
        ServerAddress serverAddress = new ServerAddress(address, port);
        MongoClientOptions clientOpt = MongoClientOptions.builder().addServerMonitorListener(new ServerMonitorListener() {
            @Override
            public void serverHearbeatStarted(ServerHeartbeatStartedEvent serverHeartbeatStartedEvent) {

            }

            @Override
            public void serverHeartbeatSucceeded(ServerHeartbeatSucceededEvent serverHeartbeatSucceededEvent) {
                delay++;
                if(delay==16) {
                    delay=0;
                    connectionValidRunner.run();
                }
            }

            @Override
            public void serverHeartbeatFailed(ServerHeartbeatFailedEvent serverHeartbeatFailedEvent) {
                delay=0;
            }
        })
                .addCommandListener(new CommandListener() {
            @Override
            public void commandStarted(CommandStartedEvent event) {

            }

            @Override
            public void commandSucceeded(CommandSucceededEvent event) {

            }

            @Override
            public void commandFailed(CommandFailedEvent event) {
                commandFailRunner.run(event);
            }
        }).writeConcern(WriteConcern.MAJORITY)
                .serverSelectionTimeout(2500).socketTimeout(2500).connectTimeout(2500)
                .heartbeatConnectTimeout(2500).heartbeatSocketTimeout(2500).heartbeatFrequency(1000).build();

        if(user==null || password==null){
            client=new MongoClient(serverAddress, clientOpt);
        }else if(authenticationDatabase==null) {
            client=new MongoClient(serverAddress, MongoCredential.createScramSha1Credential(user, database, password.toCharArray()), clientOpt);
        }else{
            client=new MongoClient(serverAddress, MongoCredential.createScramSha1Credential(user, authenticationDatabase, password.toCharArray()), clientOpt);
        }

        mongoDatabase=client.getDatabase(database);
    }

    public MongoDatabase getDatabase(){
        return mongoDatabase;
    }

    @Override
    public void close(){
        client.close();
    }
}
