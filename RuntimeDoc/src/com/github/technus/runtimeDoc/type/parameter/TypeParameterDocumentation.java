package com.github.technus.runtimeDoc.type.parameter;

import com.github.technus.runtimeDoc.AnnotatedElementDocumentation;
import com.github.technus.runtimeDoc.type.use.child.BoundDocumentation;

import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.TypeVariable;

public class TypeParameterDocumentation<T extends GenericDeclaration> extends AnnotatedElementDocumentation<TypeVariable<T>> {
    public TypeParameterDocumentation(TypeVariable<T> clazz){
        super(clazz,ElementType.TYPE_PARAMETER);

        for(AnnotatedType exception:element.getAnnotatedBounds()){
            addChild(new BoundDocumentation(exception).withParent(this));
        }
    }

    @Override
    protected String fillName() {
        return element.getName();
    }

    @Override
    protected String fillDeclaration() {
        return element.getTypeName();
    }

    @Override
    protected String fillDescriptionTag() {
        return element.getTypeName();
    }
}
