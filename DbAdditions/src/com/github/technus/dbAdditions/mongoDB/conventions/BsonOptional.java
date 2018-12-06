package com.github.technus.dbAdditions.mongoDB.conventions;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD,ElementType.FIELD,ElementType.PARAMETER})
@Repeatable(BsonOptionals.class)
@Inherited
public @interface BsonOptional {
    String value() default "";
}
