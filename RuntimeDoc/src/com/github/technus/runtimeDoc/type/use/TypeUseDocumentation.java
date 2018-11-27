package com.github.technus.runtimeDoc.type.use;

import com.github.technus.runtimeDoc.AnnotatedElementDocumentation;

import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedType;

public class TypeUseDocumentation extends AnnotatedElementDocumentation<AnnotatedType> {
    public TypeUseDocumentation(AnnotatedType type){
        super(type,ElementType.TYPE_USE);
    }

    @Override
    protected String setName() {
        return element.getType().getTypeName();
    }

    @Override
    protected String setDeclaration() {
        return element.getType().getTypeName();
    }

    @Override
    protected String setDescriptionTag() {
        return element.getType().getTypeName();
    }
}
