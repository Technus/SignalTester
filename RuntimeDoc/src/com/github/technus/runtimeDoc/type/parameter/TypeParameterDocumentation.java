package com.github.technus.runtimeDoc.type.parameter;

import com.github.technus.runtimeDoc.AnnotatedElementDocumentation;

import java.lang.annotation.ElementType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.TypeVariable;

public class TypeParameterDocumentation<T extends GenericDeclaration> extends AnnotatedElementDocumentation<TypeVariable<T>> {
    public TypeParameterDocumentation(TypeVariable<T> clazz){
        super(clazz,ElementType.TYPE_PARAMETER);
    }

    @Override
    protected String setName() {
        return element.getName();
    }

    @Override
    protected String setDeclaration() {
        return element.getTypeName();
    }

    @Override
    protected String setDescriptionTag() {
        return element.getTypeName();
    }
}
