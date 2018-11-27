package com.github.technus.runtimeDoc.type;

import com.github.technus.runtimeDoc.AnnotatedElementDocumentation;
import com.github.technus.runtimeDoc.type.parameter.TypeParameterDocumentation;

import java.lang.annotation.ElementType;
import java.lang.reflect.TypeVariable;

public class ClassDocumentation<T> extends AnnotatedElementDocumentation<Class<T>> {
    @SuppressWarnings("unchecked")
    public ClassDocumentation(Class<T> clazz){
        super(clazz,ElementType.TYPE);
        for(TypeVariable typeVariable:element.getTypeParameters()){
            addChild(new TypeParameterDocumentation(typeVariable));
        }
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
