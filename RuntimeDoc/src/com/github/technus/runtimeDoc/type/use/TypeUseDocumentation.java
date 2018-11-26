package com.github.technus.runtimeDoc.type.use;

import com.github.technus.runtimeDoc.type.ClassDocumentation;

import java.lang.annotation.ElementType;

public class TypeUseDocumentation<T> extends ClassDocumentation<T> {
    public TypeUseDocumentation(Class<T> clazz){
        super(clazz,ElementType.TYPE_USE);
    }
}
