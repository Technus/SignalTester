package com.github.technus.runtimeDoc.annotatedElement.accessibleObject;

import com.github.technus.runtimeDoc.annotatedElement.AnnotatedElementDocumentation;

import java.lang.annotation.ElementType;
import java.lang.reflect.AccessibleObject;

public abstract class AccessibleObjectDocumentation<T extends AccessibleObject> extends AnnotatedElementDocumentation<T> {
    protected AccessibleObjectDocumentation(T member, ElementType type){
        super(member,type);
    }
}
