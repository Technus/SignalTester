package com.github.technus.dbAdditions.mongoDB.pojo;

import com.github.technus.dbAdditions.mongoDB.SafePOJO;
import com.github.technus.dbAdditions.mongoDB.codecs.ClassCodec;
import com.github.technus.dbAdditions.mongoDB.codecs.StackTraceElementCodec;
import com.github.technus.dbAdditions.mongoDB.conventions.BsonRemove;
import com.mongodb.MongoClient;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

public class ThrowableLog {
    public static String currentApplicationName;
    public static final CodecRegistry THROWABLE_LOG_COLLECTION_CODECS =CodecRegistries.fromRegistries(
            MongoClient.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(
                    SafePOJO.getProviderBuilder(ThrowableLog.class, ThrowableLog.class).register(ThreadLog.class).register(ThreadGroupLog.class).build()),
            CodecRegistries.fromCodecs(ClassCodec.INSTANCE,StackTraceElementCodec.INSTANCE));

    private ObjectId id;
    private final String applicationName;

    private final Throwable throwable;
    private final Class<? extends Throwable> throwableClass;
    private final String message;
    private final ThrowableLog cause;
    private final ArrayList<ThrowableLog> suppressed;
    private final ArrayList<StackTraceElement> stackTrace;
    private final ThreadLog threadLog;
    private final Instant time;

    public ThrowableLog(Throwable t){
        this(t,null);
    }

    public ThrowableLog(Throwable t,Thread thread) {
        time=Instant.now();

        threadLog=new ThreadLog(thread==null?Thread.currentThread():thread);

        throwable = t;

        throwableClass = t.getClass();
        message = t.getMessage();

        if (t.getCause() != t && t.getCause()!=null) {
            cause = new ThrowableLog(t.getCause());
        } else {
            cause = null;
        }
        if (t.getSuppressed().length > 0) {
            suppressed = new ArrayList<>();
            for (Throwable supressedThrowable :
                    t.getSuppressed()) {
                suppressed.add(new ThrowableLog(supressedThrowable));
            }
        } else {
            suppressed = null;
        }
        applicationName = currentApplicationName;

        stackTrace = new ArrayList<>();
        stackTrace.addAll(Arrays.asList(t.getStackTrace()));
    }

    @BsonCreator
    public ThrowableLog(
            @BsonId ObjectId id,
            @BsonProperty("time") Instant time,
            @BsonProperty("threadLog") ThreadLog threadLog,
            @BsonProperty("applicationName") String applicationName,
            @BsonProperty("throwableClass") Class<? extends Throwable> throwableClass,
            @BsonProperty("message") String message,
            @BsonProperty("cause") ThrowableLog cause,
            @BsonProperty("suppressed") ArrayList<ThrowableLog> suppressed,
            @BsonProperty("stackTrace") ArrayList<StackTraceElement> stackTrace) {
        this.id=id;
        this.time=time;
        this.threadLog=threadLog;
        this.applicationName = applicationName;
        this.throwable = null;
        this.throwableClass = throwableClass;
        this.message = message;
        this.cause = cause;
        this.suppressed = suppressed;
        this.stackTrace = stackTrace;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    @BsonRemove
    public Throwable getThrowable() {
        return throwable;
    }

    public Instant getTime() {
        return time;
    }

    public Class<? extends Throwable> getThrowableClass() {
        return throwableClass;
    }

    public String getMessage() {
        return message;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public ThrowableLog getCause() {
        return cause;
    }

    public ArrayList<ThrowableLog> getSuppressed() {
        return suppressed;
    }

    public ArrayList<StackTraceElement> getStackTrace() {
        return stackTrace;
    }

    public ThreadLog getThreadLog() {
        return threadLog;
    }
}
