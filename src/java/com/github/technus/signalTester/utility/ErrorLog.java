package java.com.github.technus.signalTester.utility;

import java.com.github.technus.signalTester.db.mongoDB.conventions.BsonRemove;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorLog {
    public static String applicationName;

    @BsonId
    public ObjectId id;

    @BsonRemove
    private Throwable throwable;
    public Class<? extends Throwable> name;
    public String message;
    public String details;

    public String application;

    public ErrorLog(Throwable t){
        throwable=t;

        name=t.getClass();
        message=t.getMessage();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        details=sw.toString();

        application=applicationName;
    }

    @BsonRemove
    public Throwable getThrowable() {
        return throwable;
    }
}
