package java.com.github.technus.signalTester.db.mongoDB.conventions;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(BsonTypeRemovers.class)
public @interface BsonTypeRemove {
    Class value() default Void.class;
}
