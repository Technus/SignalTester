package com.github.technus.runtimeDoc.accessibleObject.executable;

import com.github.technus.runtimeDoc.accessibleObject.AccessibleObjectDocumentation;
import com.github.technus.runtimeDoc.parameter.ParameterDocumentation;
import com.github.technus.runtimeDoc.type.parameter.TypeParameterDocumentation;
import com.github.technus.runtimeDoc.type.use.child.ExceptionDocumentation;
import com.github.technus.runtimeDoc.type.use.child.RecieverDocumentation;
import com.github.technus.runtimeDoc.type.use.child.ReturnDocumentation;

import java.lang.annotation.ElementType;
import java.lang.reflect.*;

public abstract class ExecutableDocumentation<T extends Executable> extends AccessibleObjectDocumentation<T> {
    @SuppressWarnings("unchecked")
    protected ExecutableDocumentation(T executable, ElementType type){
        super(executable,type);
        for(Parameter parameter:element.getParameters()){
            addChild(new ParameterDocumentation(parameter).withParent(this));
        }
        for(TypeVariable typeVariable:element.getTypeParameters()){
            addChild(new TypeParameterDocumentation(typeVariable).withParent(this));
        }

        AnnotatedType annotatedType=element.getAnnotatedReceiverType();
        if(annotatedType!=null){
            addChild(new RecieverDocumentation(annotatedType).withParent(this));
        }
        annotatedType=element.getAnnotatedReturnType();
        if(annotatedType!=null){
            addChild(new ReturnDocumentation(annotatedType).withParent(this));
        }

        for(AnnotatedType exception:element.getAnnotatedExceptionTypes()){
            addChild(new ExceptionDocumentation(exception).withParent(this));
        }
    }
}
