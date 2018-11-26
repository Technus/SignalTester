package com.github.technus.runtimeDoc.type;

import com.github.technus.runtimeDoc.AnnotatedElementDocumentation;

import java.lang.annotation.ElementType;

public class ClassDocumentation<T> extends AnnotatedElementDocumentation<Class<T>> {
    public ClassDocumentation(Class<T> clazz){
        super(clazz,ElementType.TYPE);
    }

    protected ClassDocumentation(Class<T> clazz,ElementType type){
        super(clazz,type);
    }

    @Override
    protected String setName() {
        String name=element.getSimpleName();
        if(name.length()==0){
            return element.getTypeName();
        }
        return name;
    }

    @Override
    protected String setDeclaration() {
        return element.getCanonicalName();
    }

    @Override
    protected String setDescriptionTag() {
        return element.getName();
    }
}
