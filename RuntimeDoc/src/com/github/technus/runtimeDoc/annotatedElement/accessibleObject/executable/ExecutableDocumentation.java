package com.github.technus.runtimeDoc.annotatedElement.accessibleObject.executable;

import com.github.technus.runtimeDoc.annotatedElement.accessibleObject.AccessibleObjectDocumentation;
import com.github.technus.runtimeDoc.annotatedElement.parameter.ParameterDocumentation;
import com.github.technus.runtimeDoc.annotatedElement.type.parameter.TypeParameterDocumentation;
import com.github.technus.runtimeDoc.annotatedElement.type.use.child.ExceptionDocumentation;
import com.github.technus.runtimeDoc.annotatedElement.type.use.child.RecieverDocumentation;
import com.github.technus.runtimeDoc.annotatedElement.type.use.child.ReturnDocumentation;

import java.lang.annotation.ElementType;
import java.lang.reflect.*;

public abstract class ExecutableDocumentation<T extends Executable> extends AccessibleObjectDocumentation<T> {
    @SuppressWarnings("unchecked")
    protected ExecutableDocumentation(T executable, ElementType type){
        super(executable,type);
        for(Parameter parameter:element.getParameters()){
            addChild(new ParameterDocumentation(parameter));
        }
        for(TypeVariable typeVariable:element.getTypeParameters()){
            addChild(new TypeParameterDocumentation(typeVariable).getIfAnnotated());
        }

        AnnotatedType annotatedType=element.getAnnotatedReceiverType();
        if(annotatedType!=null){
            addChild(new RecieverDocumentation(annotatedType).getIfAnnotated());
        }
        annotatedType=element.getAnnotatedReturnType();
        if(annotatedType!=null){
            addChild(new ReturnDocumentation(annotatedType));
        }

        for(AnnotatedType exception:element.getAnnotatedExceptionTypes()){
            addChild(new ExceptionDocumentation(exception));
        }
    }
}
