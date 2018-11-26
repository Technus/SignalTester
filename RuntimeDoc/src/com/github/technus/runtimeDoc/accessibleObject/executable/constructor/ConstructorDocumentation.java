package com.github.technus.runtimeDoc.accessibleObject.executable.constructor;

import com.github.technus.runtimeDoc.accessibleObject.executable.ExecutableDocumentation;

import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;

public class ConstructorDocumentation<T> extends ExecutableDocumentation<Constructor<T>> {
    public ConstructorDocumentation(Constructor<T> constructor){
        super(constructor,ElementType.CONSTRUCTOR);
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
