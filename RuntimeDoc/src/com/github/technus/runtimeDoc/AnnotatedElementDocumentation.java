package com.github.technus.runtimeDoc;

import javafx.scene.control.TreeItem;

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
    protected final Set<AnnotatedElementDocumentation> children=new HashSet<>();

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
            name= fillName();
        }
        if(declaration==null || declaration.length()==0){
            declaration= fillDeclaration();
        }
        if(descriptionTag==null || descriptionTag.length()==0){
            descriptionTag= fillDescriptionTag();
        }
    }

    protected abstract String fillName();

    protected abstract String fillDeclaration();

    protected abstract String fillDescriptionTag();

    public void setName(String name) {
        this.name = name;
    }

    public void setDeclaration(String declaration) {
        this.declaration = declaration;
    }

    public void setDescriptionTag(String descriptionTag) {
        this.descriptionTag = descriptionTag;
    }

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
                try{
                    String resource=resourceBundle.getString(getDescriptionTag());
                    if(resource.length()==0){
                        return tag;
                    }
                    return resource;
                }catch (MissingResourceException|NullPointerException e){
                    return tag;
                }
            }
        }
        return null;
    }

    public final String getDescription(){
        return getDescription(Locale.getDefault());
    }

    public final AnnotatedElementDocumentation<T> withParent(AnnotatedElementDocumentation parent) {
        setParent(parent);
        return this;
    }

    public final void setParent(AnnotatedElementDocumentation parent) {
        if(this.parent==null && parent!=null) {
            this.parent = parent;
            if(!parent.getChildren().contains(this)){
                parent.addChild(this);
            }
        }
    }

    public final AnnotatedElementDocumentation getParent() {
        return parent;
    }

    public final void addChild(AnnotatedElementDocumentation child){
        if(child!=null) {
            child.setParent(this);
        }
    }

    public final Set<AnnotatedElementDocumentation> getChildren() {
        return Collections.unmodifiableSet(children);
    }

    public String getDocumentationTypeTag(){
        return getClass().getSimpleName().replace("Documentation","");
    }

    public final String getDocumentationType(){
        return getDocumentationType(Locale.getDefault());
    }

    public String getDocumentationType(Locale locale){
        String tag=getDescriptionTag();
        if(tag!=null) {
            Class clazz = getClass();
            ResourceBundle resourceBundle = ResourceBundle.getBundle(clazz.getSimpleName(), locale, clazz.getClassLoader());
            try{
                String resource=resourceBundle.getString(getDescriptionTag());
                if(resource.length()==0){
                    return tag;
                }
                return resource;
            }catch (MissingResourceException|NullPointerException e) {
                return tag;
            }
        }
        return null;
    }

    @Override
    public final int hashCode() {
        return element.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return obj.getClass() == this.getClass() && element.equals(obj);
    }

    public final TreeItem<AnnotatedElementDocumentation> getTreeItem(){
        TreeItem<AnnotatedElementDocumentation> item= new TreeItem<>();
        item.setValue(this);
        return item;
    }

    @SuppressWarnings("unchecked")
    public final TreeItem<AnnotatedElementDocumentation> buildTreeItem(){
        TreeItem<AnnotatedElementDocumentation> item=getTreeItem();
        children.forEach(children->item.getChildren().add(children.buildTreeItem()));
        return null;
    }

    public final TreeItem<AnnotatedElementDocumentation> buildTreeRoot(){
        if(parent==null){
            return buildTreeItem();
        }
        return null;
    }
}
