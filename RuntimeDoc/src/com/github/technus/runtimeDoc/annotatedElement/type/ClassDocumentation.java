package com.github.technus.runtimeDoc.annotatedElement.type;

import com.github.technus.runtimeDoc.annotatedElement.AnnotatedElementDocumentation;
import com.github.technus.runtimeDoc.annotatedElement.accessibleObject.executable.constructor.ConstructorDocumentation;
import com.github.technus.runtimeDoc.annotatedElement.accessibleObject.executable.method.MethodDocumentation;
import com.github.technus.runtimeDoc.annotatedElement.accessibleObject.field.FieldDocumentation;
import com.github.technus.runtimeDoc.annotatedElement.type.parameter.TypeParameterDocumentation;
import com.github.technus.runtimeDoc.annotatedElement.type.use.child.InterfaceDocumentation;
import com.github.technus.runtimeDoc.annotatedElement.type.use.child.SuperClassDocumentation;

import java.lang.annotation.ElementType;
import java.lang.reflect.*;

public class ClassDocumentation extends AnnotatedElementDocumentation<Class> {
    @SuppressWarnings("unchecked")
    public ClassDocumentation(Class clazz){
        super(clazz,ElementType.TYPE);
        for(TypeVariable typeVariable:element.getTypeParameters()){
            addChild(new TypeParameterDocumentation(typeVariable).getIfAnnotated());
        }
        AnnotatedType annotatedType=element.getAnnotatedSuperclass();
        if(annotatedType!=null){
            addChild(new SuperClassDocumentation(annotatedType).getIfAnnotated());
        }
        for(AnnotatedType exception:element.getAnnotatedInterfaces()){
            addChild(new InterfaceDocumentation(exception).getIfAnnotated());
        }
        for(Method method:element.getMethods()){
            addChild(new MethodDocumentation(method).getIfAnnotated());
        }
        for(Constructor constructor:element.getConstructors()){
            addChild(new ConstructorDocumentation(constructor).getIfAnnotated());
        }
        for(Field field:element.getFields()){
            addChild(new FieldDocumentation(field).getIfAnnotated());
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
