package com.github.technus.runtimeDoc.annotatedElement.accessibleObject.field;

import com.github.technus.runtimeDoc.annotatedElement.accessibleObject.AccessibleObjectDocumentation;
import com.github.technus.runtimeDoc.annotatedElement.type.use.child.TypeDocumentation;

import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;

public class FieldDocumentation extends AccessibleObjectDocumentation<Field> {
    public FieldDocumentation(Field field){
        super(field,ElementType.FIELD);

        AnnotatedType annotatedType=element.getAnnotatedType();
        if(annotatedType!=null){
            addChild(new TypeDocumentation(annotatedType));
        }
    }

    @Override
    protected String fillName() {
        return element.getName();
    }

    @Override
    protected String fillDeclaration() {
        return element.toGenericString();
    }

    @Override
    protected String fillDescriptionTag() {
        return element.toGenericString();
    }
}
