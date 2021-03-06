package com.github.technus.runtimeDoc.annotatedElement.parameter;

import com.github.technus.runtimeDoc.annotatedElement.AnnotatedElementDocumentation;
import com.github.technus.runtimeDoc.annotatedElement.type.use.child.TypeDocumentation;

import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Parameter;

public class ParameterDocumentation extends AnnotatedElementDocumentation<Parameter> {
    public ParameterDocumentation(Parameter parameter){
        super(parameter,ElementType.PARAMETER);

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
        return element.toString();
    }

    @Override
    protected String fillDescriptionTag() {
        return element.toString();
    }
}
