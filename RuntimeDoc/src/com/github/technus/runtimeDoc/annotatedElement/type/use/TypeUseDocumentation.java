package com.github.technus.runtimeDoc.annotatedElement.type.use;

import com.github.technus.runtimeDoc.annotatedElement.AnnotatedElementDocumentation;
import com.github.technus.runtimeDoc.annotatedElement.type.use.child.*;

import java.lang.annotation.ElementType;
import java.lang.reflect.*;

public class TypeUseDocumentation extends AnnotatedElementDocumentation<AnnotatedType> {
    public TypeUseDocumentation(AnnotatedType type){
        super(type,ElementType.TYPE_USE);
        if(element instanceof AnnotatedArrayType){
            AnnotatedType annotatedType=((AnnotatedArrayType) element).getAnnotatedGenericComponentType();
            if(annotatedType!=null){
                addChild(new GenericComponentDocumentation(annotatedType).getIfAnnotated());
            }
        }
        if(element instanceof AnnotatedParameterizedType){
            for(AnnotatedType exception:((AnnotatedParameterizedType) element).getAnnotatedActualTypeArguments()){
                addChild(new TypeArgumentDocumentation(exception).getIfAnnotated());
            }
        }
        if(element instanceof AnnotatedWildcardType){
            for(AnnotatedType exception:((AnnotatedWildcardType) element).getAnnotatedLowerBounds()){
                addChild(new LowerBoundDocumentation(exception).getIfAnnotated());
            }
            for(AnnotatedType exception:((AnnotatedWildcardType) element).getAnnotatedUpperBounds()){
                addChild(new UpperBoundDocumentation(exception).getIfAnnotated());
            }
        }
        if(element instanceof AnnotatedTypeVariable){
            for(AnnotatedType exception:((AnnotatedTypeVariable) element).getAnnotatedBounds()){
                addChild(new BoundDocumentation(exception).getIfAnnotated());
            }
        }
    }

    @Override
    protected String fillName() {
        return element.getType().getTypeName();
    }

    @Override
    protected String fillDeclaration() {
        return element.getType().getTypeName();
    }

    @Override
    protected String fillDescriptionTag() {
        return element.getType().getTypeName();
    }
}
