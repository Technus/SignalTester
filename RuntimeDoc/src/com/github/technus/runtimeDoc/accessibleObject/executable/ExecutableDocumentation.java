package com.github.technus.runtimeDoc.accessibleObject.executable;

import com.github.technus.runtimeDoc.accessibleObject.AccessibleObjectDocumentation;
import com.github.technus.runtimeDoc.parameter.ParameterDocumentation;
import com.github.technus.runtimeDoc.type.parameter.TypeParameterDocumentation;

import java.lang.annotation.ElementType;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.lang.reflect.TypeVariable;

public abstract class ExecutableDocumentation<T extends Executable> extends AccessibleObjectDocumentation<T> {
    @SuppressWarnings("unchecked")
    protected ExecutableDocumentation(T executable, ElementType type){
        super(executable,type);
        for(Parameter parameter:element.getParameters()){
            addChild(new ParameterDocumentation(parameter));
        }
        for(TypeVariable typeVariable:element.getTypeParameters()){
            addChild(new TypeParameterDocumentation(typeVariable));
        }
    }
}
