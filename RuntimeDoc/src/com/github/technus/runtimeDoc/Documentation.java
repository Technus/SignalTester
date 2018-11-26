package com.github.technus.runtimeDoc;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Documentation {
    ElementType value();
}
