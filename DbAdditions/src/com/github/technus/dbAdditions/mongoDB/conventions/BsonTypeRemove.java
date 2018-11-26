package com.github.technus.dbAdditions.mongoDB.conventions;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(BsonTypeRemovers.class)
@Inherited
public @interface BsonTypeRemove {
    Class value() default Void.class;
}
