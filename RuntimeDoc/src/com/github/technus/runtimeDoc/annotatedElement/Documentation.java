package com.github.technus.runtimeDoc.annotatedElement;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Documentation {
    ElementType value();
}
