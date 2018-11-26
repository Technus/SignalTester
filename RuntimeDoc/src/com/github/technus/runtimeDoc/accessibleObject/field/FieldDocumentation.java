package com.github.technus.runtimeDoc.accessibleObject.field;

import com.github.technus.runtimeDoc.accessibleObject.AccessibleObjectDocumentation;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;

public class FieldDocumentation extends AccessibleObjectDocumentation<Field> {
    public FieldDocumentation(Field field){
        super(field,ElementType.FIELD);
    }

    @Override
    protected String setName() {
        return element.getName();
    }

    @Override
    protected String setDeclaration() {
        return element.toGenericString();
    }

    @Override
    protected String setDescriptionTag() {
        return element.toGenericString();
    }
}
