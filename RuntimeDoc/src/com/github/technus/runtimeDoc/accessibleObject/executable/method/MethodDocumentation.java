package com.github.technus.runtimeDoc.accessibleObject.executable.method;

import com.github.technus.runtimeDoc.accessibleObject.executable.ExecutableDocumentation;

import java.lang.annotation.ElementType;
import java.lang.reflect.Method;

public class MethodDocumentation extends ExecutableDocumentation<Method> {
    public MethodDocumentation(Method method){
        super(method,ElementType.METHOD);
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
