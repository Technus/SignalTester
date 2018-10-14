package com.github.technus.dbAdditions.mongoDB.conventions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BsonTypeRemovers {
    BsonTypeRemove[] value();
}
