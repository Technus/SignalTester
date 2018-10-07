package java.com.github.technus.signalTester.db.mongoDB;


import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.event.*;
import org.bson.Document;

public class MongoDatabaseHandler {
    private String address;
    private int port;

    private ServerAddress serverAddress;
    private MongoCredential login;
    private MongoClient client;
    private MongoClientOptions clientOpt;
    private MongoDatabase database;
    private boolean status;

    public MongoDatabaseHandler(String address,int port,String user,String databaseName,String password,ICommandFailRunner runner) throws MongoException {
        this.address=address;
        this.port=port;

        serverAddress=new ServerAddress(address,port);
        //serverAddress.getSocketAddress();
        login= MongoCredential.createScramSha1Credential(user,databaseName,password.toCharArray());
        clientOpt= MongoClientOptions.builder().addServerMonitorListener(new ServerMonitorListener() {
            @Override
            public void serverHearbeatStarted(ServerHeartbeatStartedEvent serverHeartbeatStartedEvent) {

            }

            @Override
            public void serverHeartbeatSucceeded(ServerHeartbeatSucceededEvent serverHeartbeatSucceededEvent) {
                status=true;
            }

            @Override
            public void serverHeartbeatFailed(ServerHeartbeatFailedEvent serverHeartbeatFailedEvent) {
                status=false;
            }
        }).addCommandListener(new CommandListener() {
            @Override
            public void commandStarted(CommandStartedEvent event) {

            }

            @Override
            public void commandSucceeded(CommandSucceededEvent event) {

            }

            @Override
            public void commandFailed(CommandFailedEvent event) {
                runner.run(event);
            }
        }).writeConcern(WriteConcern.ACKNOWLEDGED)
                .serverSelectionTimeout(2500).socketTimeout(2500).connectTimeout(2500)
                .heartbeatConnectTimeout(2500).heartbeatSocketTimeout(2500)
                .heartbeatFrequency(5000).build();

        client=new MongoClient(serverAddress, login,clientOpt);
        database=client.getDatabase(databaseName);

        database.runCommand(new Document().append("ping", ""));
    }

    public MongoCollection<Document> getCollectionHandler(String collectionName){
        return database.getCollection(collectionName);
    }

    public <TDocument> MongoCollection<TDocument> getCollectionHandler(String collectionName, Class<TDocument> documentClass){
        return database.getCollection(collectionName,documentClass);
    }

    public void close() throws Exception{
        client.close();
    }
}
