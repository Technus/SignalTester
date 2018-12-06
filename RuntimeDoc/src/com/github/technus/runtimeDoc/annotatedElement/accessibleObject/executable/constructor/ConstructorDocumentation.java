package com.github.technus.runtimeDoc.annotatedElement.accessibleObject.executable.constructor;

import com.github.technus.runtimeDoc.annotatedElement.accessibleObject.executable.ExecutableDocumentation;

import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;

public class ConstructorDocumentation extends ExecutableDocumentation<Constructor> {
    public ConstructorDocumentation(Constructor constructor){
        super(constructor,ElementType.CONSTRUCTOR);
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
