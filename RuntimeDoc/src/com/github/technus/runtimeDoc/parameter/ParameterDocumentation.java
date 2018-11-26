package com.github.technus.runtimeDoc.parameter;

import com.github.technus.runtimeDoc.AnnotatedElementDocumentation;

import java.lang.annotation.ElementType;
import java.lang.reflect.Parameter;

public class ParameterDocumentation extends AnnotatedElementDocumentation<Parameter> {
    public ParameterDocumentation(Parameter parameter){
        super(parameter,ElementType.PARAMETER);
    }

    @Override
    protected String setName() {
        return element.getName();
    }

    @Override
    protected String setDeclaration() {
        return element.toString();
    }

    @Override
    protected String setDescriptionTag() {
        return element.toString();
    }
}
