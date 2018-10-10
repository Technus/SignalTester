package com.github.technus.dbAdditions.mongoDB.pojo;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class ThreadGroupLog {
    private final int maxPriority;
    private final String name;
    private final ThreadGroupLog parent;
    private final int activeCount,activeGroupCount;
    private final boolean daemon,destroyed;

    public ThreadGroupLog(ThreadGroup tg){
        maxPriority=tg.getMaxPriority();
        name=tg.getName();
        if(tg.getParent()!=null){
            parent=new ThreadGroupLog(tg.getParent());
        }else {
            parent=null;
        }
        activeCount=tg.activeCount();
        activeGroupCount=tg.activeGroupCount();
        daemon=tg.isDaemon();
        destroyed=tg.isDestroyed();
    }

    @BsonCreator
    public ThreadGroupLog(
            @BsonProperty("maxPriority") int maxPriority,
            @BsonProperty("name") String name,
            @BsonProperty("parent") ThreadGroupLog parent,
            @BsonProperty("activeCount") int activeCount,
            @BsonProperty("activeGroupCount") int activeGroupCount,
            @BsonProperty("daemon") boolean daemon,
            @BsonProperty("destroyed") boolean destroyed) {
        this.maxPriority = maxPriority;
        this.name = name;
        this.parent = parent;
        this.activeCount = activeCount;
        this.activeGroupCount = activeGroupCount;
        this.daemon = daemon;
        this.destroyed = destroyed;
    }

    public int getMaxPriority() {
        return maxPriority;
    }

    public String getName() {
        return name;
    }

    public ThreadGroupLog getParent() {
        return parent;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public int getActiveGroupCount() {
        return activeGroupCount;
    }

    public boolean isDaemon() {
        return daemon;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
