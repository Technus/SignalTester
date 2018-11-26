package com.github.technus.runtimeDoc;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public abstract class AnnotatedElementDocumentation<T extends AnnotatedElement> {
    protected T element;
    protected Annotation annotation;
    protected ElementType type;
    protected String name,declaration,descriptionTag;
    protected AnnotatedElementDocumentation parent;
    protected final List<AnnotatedElementDocumentation> children=new ArrayList<>();

    protected AnnotatedElementDocumentation(T element,ElementType type) {
        this.element=element;
        this.type=type;
        for(Annotation elementAnnotation:element.getAnnotations()){
            Documentation documentationAnnotation=elementAnnotation.annotationType().getAnnotation(Documentation.class);
            if(documentationAnnotation!=null && documentationAnnotation.value()==type) {
                if(this.annotation==null) {
                    this.annotation = elementAnnotation;
                }else {
                    throw new RuntimeException("Multiple Documentation found for "+element.toString()+" with specified type "+type.toString());
                }
            }
        }
        if (annotation != null) {
            readAnnotation();
        }
        fillInformation();
    }

    protected AnnotatedElementDocumentation(T element) {
        this.element=element;
        for(Annotation elementAnnotation:element.getAnnotations()){
            Documentation documentationAnnotation=elementAnnotation.annotationType().getAnnotation(Documentation.class);
            if(documentationAnnotation!=null) {
                if(this.annotation==null) {
                    this.annotation = elementAnnotation;
                    this.type = documentationAnnotation.value();
                }else {
                    throw new RuntimeException("Multiple Documentation found for "+element.toString());
                }
            }
        }
        if (annotation != null) {
            readAnnotation();
        }
        fillInformation();
    }

    protected void readAnnotation(){
        try {
            Method getName = annotation.getClass().getMethod("name");
            name=getName.invoke(annotation).toString();
        }catch (NoSuchMethodException| IllegalAccessException | InvocationTargetException e){
            throw new AnnotationFormatError("Unable to read name from "+annotation.toString(),e);
        }
        try {
            Method getDeclaration = annotation.getClass().getMethod("declaration");
            declaration=getDeclaration.invoke(annotation).toString();
        }catch (NoSuchMethodException| IllegalAccessException | InvocationTargetException e){
            throw new AnnotationFormatError("Unable to read declaration from "+annotation.toString(),e);
        }
        try {
            Method getDescriptionTag = annotation.getClass().getMethod("descriptionTag");
            descriptionTag =getDescriptionTag.invoke(annotation).toString();
        }catch (NoSuchMethodException| IllegalAccessException | InvocationTargetException e){
            throw new AnnotationFormatError("Unable to read description tag from "+annotation.toString(),e);
        }
    }

    /**
     * Should fill name, declaration and descriptionTag if they are null or "" only when actually possible
     */
    private void fillInformation(){
        if(name==null || name.length()==0){
            name=setName();
        }
        if(declaration==null || declaration.length()==0){
            declaration=setDeclaration();
        }
        if(descriptionTag==null || descriptionTag.length()==0){
            descriptionTag=setDescriptionTag();
        }
    }

    protected abstract String setName();

    protected abstract String setDeclaration();

    protected abstract String setDescriptionTag();

    public final T getElement() {
        return element;
    }

    public final Annotation getAnnotation() {
        return annotation;
    }

    public final ElementType getType() {
        return type;
    }

    public final String getName(){
        return name;
    }

    public final String getDeclaration(){
        return declaration;
    }

    public final String getDescriptionTag(){
        if (descriptionTag != null && descriptionTag.length() != 0) {
            return descriptionTag;
        }
        if (declaration != null && declaration.length() != 0) {
            return declaration;
        }
        if (name != null && name.length() != 0) {
            return name;
        }
        return null;
    }

    protected Class getEnclosingClass(){
        if(type==ElementType.TYPE) {
            Class clazz = (Class) element;
            String name = clazz.getName();
            if (name != null && !name.contains("$") && name.length() != 0) {
                return clazz;
            }
        }
        AnnotatedElementDocumentation parent=getParent();
        return parent == null ? null : parent.getEnclosingClass();
    }

    public String getDescription(Locale locale){
        String tag=getDescriptionTag();
        if(tag!=null) {
            Class enclosingClass = getEnclosingClass();
            if (enclosingClass != null) {
                ResourceBundle resourceBundle = ResourceBundle.getBundle(enclosingClass.getSimpleName(), locale, enclosingClass.getClassLoader());
                return resourceBundle.getString(getDescriptionTag());
            }
        }
        return null;
    }

    public final String getDescription(){
        return getDescription(Locale.getDefault());
    }

    public void setParent(AnnotatedElementDocumentation parent) {
        if(this.parent==null) {
            this.parent = parent;
        }
    }

    public AnnotatedElementDocumentation getParent() {
        return parent;
    }

    public void addChild(AnnotatedElementDocumentation child){
        children.add(child);
    }

    public List<AnnotatedElementDocumentation> getChildren() {
        return Collections.unmodifiableList(children);
    }
}
