package com.github.technus.dbAdditions.mongoDB.pojo;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class ThreadLog {
    private final String name;
    private final long threadId;
    private final int priority;
    private final Thread.State state;
    private final ThreadGroupLog threadGroup;
    private final boolean alive,daemon,interrupted;

    public ThreadLog(Thread t){
        name=t.getName();
        threadId =t.getId();
        priority=t.getPriority();
        state=t.getState();
        threadGroup=new ThreadGroupLog(t.getThreadGroup());
        alive=t.isAlive();
        daemon=t.isDaemon();
        interrupted=t.isInterrupted();
    }

    @BsonCreator
    public ThreadLog(
            @BsonProperty("name") String name,
            @BsonProperty("threadId") long threadId,
            @BsonProperty("priority") int priority,
            @BsonProperty("state") Thread.State state,
            @BsonProperty("threadGroup") ThreadGroupLog threadGroup,
            @BsonProperty("alive") boolean alive,
            @BsonProperty("daemon") boolean daemon,
            @BsonProperty("interrupted") boolean interrupted) {
        this.name = name;
        this.threadId = threadId;
        this.priority = priority;
        this.state = state;
        this.threadGroup = threadGroup;
        this.alive=alive;
        this.daemon=daemon;
        this.interrupted=interrupted;
    }

    public String getName() {
        return name;
    }

    public long getThreadId() {
        return threadId;
    }

    public int getPriority() {
        return priority;
    }

    public Thread.State getState() {
        return state;
    }

    public ThreadGroupLog getThreadGroup() {
        return threadGroup;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isDaemon() {
        return daemon;
    }

    public boolean isInterrupted() {
        return interrupted;
    }
}
