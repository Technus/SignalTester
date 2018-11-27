package com.github.technus.runtimeDoc.type;

import com.github.technus.runtimeDoc.AnnotatedElementDocumentation;
import com.github.technus.runtimeDoc.type.parameter.TypeParameterDocumentation;
import com.github.technus.runtimeDoc.type.use.child.InterfaceDocumentation;
import com.github.technus.runtimeDoc.type.use.child.SuperClassDocumentation;

import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.TypeVariable;

public class ClassDocumentation extends AnnotatedElementDocumentation<Class> {
    @SuppressWarnings("unchecked")
    public ClassDocumentation(Class clazz){
        super(clazz,ElementType.TYPE);
        for(TypeVariable typeVariable:element.getTypeParameters()){
            addChild(new TypeParameterDocumentation(typeVariable).withParent(this));
        }
        AnnotatedType annotatedType=element.getAnnotatedSuperclass();
        if(annotatedType!=null){
            addChild(new SuperClassDocumentation(annotatedType).withParent(this));
        }
        for(AnnotatedType exception:element.getAnnotatedInterfaces()){
            addChild(new InterfaceDocumentation(exception).withParent(this));
        }
    }

    @Override
    protected String fillName() {
        String name=element.getSimpleName();
        if(name.length()==0){
            return element.getTypeName();
        }
        return name;
    }

    @Override
    protected String fillDeclaration() {
        return element.getCanonicalName();
    }

    @Override
    protected String fillDescriptionTag() {
        return element.getName();
    }
}
