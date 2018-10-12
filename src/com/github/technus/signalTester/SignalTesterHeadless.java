package com.github.technus.signalTester;

import com.github.technus.dbAdditions.mongoDB.MongoClientHandler;
import com.github.technus.dbAdditions.mongoDB.SafePOJO;
import com.github.technus.dbAdditions.mongoDB.pojo.ThrowableLog;
import com.github.technus.signalTester.utility.Utility;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sun.security.auth.module.NTSystem;
import javafx.scene.layout.BorderPane;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;

import java.awt.*;
import java.io.File;
import java.net.InetAddress;
import java.util.Locale;

import static com.github.technus.dbAdditions.mongoDB.pojo.ThrowableLog.THROWABLE_LOG_COLLECTION_CODECS;

public class SignalTesterHeadless implements AutoCloseable{
    private final MongoClientHandler localClient, remoteClient;
    private final MongoCollection<ThrowableLog> throwableLogCollectionLocal,throwableLogCollectionRemote;
    private final MongoCollection<Configuration> configurationCollectionLocal,configurationCollectionRemote;

    private BorderPane mainPane;

    public SignalTesterHeadless() {
        ThrowableLog.currentApplicationName ="SignalTester";

        Utility.throwableConsumer = throwable -> {
            if(getThrowableLogCollectionLocal()!=null) {
                getThrowableLogCollectionLocal().insertOne(new ThrowableLog(throwable));
            }else {

            }
        };

        CodecRegistry configurationCollectionCodecs=
                SafePOJO.buildCodecRegistry(Configuration.class,Configuration.class,Configuration.class);//discriminate

        localClient =new MongoClientHandler("localhost",27017,"tecAppsLocal",commandFailedEvent -> {
            System.out.println(commandFailedEvent.getCommandName());
        },()->{

        });
        remoteClient =new MongoClientHandler("localhost",27017,"tecAppsRemote", commandFailedEvent -> {
            System.out.println(commandFailedEvent.getCommandName());
        },()->{
            if(getThrowableLogCollectionLocal()!=null && getThrowableLogCollectionRemote()!=null) {
                for (ThrowableLog log : getThrowableLogCollectionLocal().find()) {
                    ObjectId id=log.getId();
                    try {
                        log.setId(null);
                        getThrowableLogCollectionRemote().insertOne(log);
                        getThrowableLogCollectionLocal().deleteOne(new Document().append("_id",id));
                    }catch (Exception e){

                        return;
                    }
                }
            }
        });
        MongoDatabase localDatabase = localClient.getDatabase();
        MongoDatabase remoteDatabase = remoteClient.getDatabase();

        throwableLogCollectionLocal= localDatabase.getCollection(ThrowableLog.class.getSimpleName(),ThrowableLog.class)
                .withCodecRegistry(THROWABLE_LOG_COLLECTION_CODECS);
        throwableLogCollectionRemote= remoteDatabase.getCollection(ThrowableLog.class.getSimpleName(),ThrowableLog.class)
                .withCodecRegistry(THROWABLE_LOG_COLLECTION_CODECS);
        configurationCollectionLocal= localDatabase.getCollection(Configuration.class.getSimpleName(),Configuration.class)
                .withCodecRegistry(configurationCollectionCodecs);
        configurationCollectionRemote= remoteDatabase.getCollection(Configuration.class.getSimpleName(),Configuration.class)
                .withCodecRegistry(configurationCollectionCodecs);
    }

    @Override
    public void close() {
        localClient.close();
        remoteClient.close();
    }

    public MongoCollection<ThrowableLog> getThrowableLogCollectionLocal() {
        return throwableLogCollectionLocal;
    }

    public MongoCollection<ThrowableLog> getThrowableLogCollectionRemote() {
        return throwableLogCollectionRemote;
    }

    public MongoCollection<Configuration> getConfigurationCollectionLocal() {
        return configurationCollectionLocal;
    }

    public MongoCollection<Configuration> getConfigurationCollectionRemote() {
        return configurationCollectionRemote;
    }

    public static void main(String... args) {
        Locale.setDefault(Locale.US);
        showSplashScreen(args);
        try{
            new SignalTesterHeadless();
        }catch (Throwable t){
            Utility.showThrowableMain(t,"Unhandled throwable!");
            System.exit(0);
        }
    }
    
    public static void showSplashScreen(String... args){
        try {
            SplashScreen splashScreen=SplashScreen.getSplashScreen();
            if(splashScreen!=null){
                Graphics2D graphics2D=splashScreen.createGraphics();
                if(graphics2D!=null){
                    graphics2D.setComposite(AlphaComposite.Clear);
                    //g.fillRect(130,250,280,40);
                    graphics2D.setPaintMode();
                    graphics2D.setColor(Color.CYAN);
                    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    graphics2D.setFont(new Font("Consolas",Font.BOLD,55));
                    FontMetrics metrics=graphics2D.getFontMetrics();
                    int lastY;
                    graphics2D.drawString("Signal Tester", metrics.getMaxAdvance(), lastY=metrics.getMaxAscent()+metrics.getMaxDescent());
                    if(args!=null && args[0] != null && !"_".equals(args[0])){
                        File f=new File(args[0]);
                        graphics2D.setFont(new Font("Consolas",Font.BOLD,30));
                        metrics=graphics2D.getFontMetrics();
                        graphics2D.drawString(f.getName().replaceFirst("\\.test\\.xml",""), metrics.getMaxAdvance(), lastY+metrics.getMaxAscent()+metrics.getMaxDescent()+metrics.getMaxDescent());
                    }
                    splashScreen.update();
                }
            }
        } catch (Exception e) {
            Utility.showThrowableMain(e,"Cannot load splash screen!");
            System.exit(0);
        }
    }

    public static NTSystem ntSystem=new NTSystem();
    public static String getUserName(){
        return ntSystem.getDomain()+"\\"+ntSystem.getName();
    }

    public static String getSystemName(){
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Throwable e) {
            return null;
        }
    }

    public static String getFullName(){
        return getSystemName()+"\\"+getUserName();
    }
}
