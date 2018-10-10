package com.github.technus.dbAdditions.mongoDB.conventions;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD,ElementType.FIELD})
@Repeatable(BsonRemovers.class)
public @interface BsonRemove {
    String value() default "";
}
