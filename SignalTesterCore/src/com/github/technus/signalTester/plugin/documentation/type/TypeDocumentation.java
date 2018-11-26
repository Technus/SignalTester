package com.github.technus.signalTester.plugin.documentation.type;

import com.github.technus.signalTester.plugin.documentation.AnnotatedElementDocumentation;

import java.lang.reflect.*;

public abstract class TypeDocumentation<T extends Type & AnnotatedElement> extends AnnotatedElementDocumentation<T> {
    public TypeDocumentation(Class<T> tClass){
        this.tClass=tClass;
    }
}
