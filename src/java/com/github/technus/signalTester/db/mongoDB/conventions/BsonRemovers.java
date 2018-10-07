package java.com.github.technus.signalTester.db.mongoDB.conventions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BsonRemovers {
    BsonRemove[] value();
}
