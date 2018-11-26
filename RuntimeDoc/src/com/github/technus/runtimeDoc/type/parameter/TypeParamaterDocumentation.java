package com.github.technus.runtimeDoc.type.parameter;

import com.github.technus.runtimeDoc.type.ClassDocumentation;

import java.lang.annotation.ElementType;

public class TypeParamaterDocumentation<T> extends ClassDocumentation<T> {
    public TypeParamaterDocumentation(Class<T> clazz){
        super(clazz,ElementType.TYPE_PARAMETER);
    }
}
