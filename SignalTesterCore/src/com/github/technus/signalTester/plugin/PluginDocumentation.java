package com.github.technus.signalTester.plugin;

import com.github.technus.runtimeDoc.AnnotatedElementDocumentation;
import com.github.technus.runtimeDoc.accessibleObject.executable.constructor.ConstructorDocumentation;
import com.github.technus.runtimeDoc.accessibleObject.executable.method.MethodDocumentation;
import com.github.technus.runtimeDoc.accessibleObject.field.FieldDocumentation;
import com.github.technus.runtimeDoc.pack.PackageDocumentation;
import com.github.technus.runtimeDoc.type.ClassDocumentation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class PluginDocumentation<T extends Plugin> {
    public final Plugin<T> plugin;
    public final HashMap<Class,ClassDocumentation> classes=new HashMap<>();
    public final HashMap<Package,PackageDocumentation> packages=new HashMap<>();
    public final HashMap<Field, FieldDocumentation> fields=new HashMap<>();
    public final HashMap<Method, MethodDocumentation> methods=new HashMap<>();
    public final HashMap<Constructor,ConstructorDocumentation> constructors=new HashMap<>();

    public PluginDocumentation(Plugin<T> plugin){
        this.plugin=plugin;
        loadAllDocumentation();
        mapDocumentation();
        linkDocumentation();
    }

    /**
     * Add docs to maps
     */
    protected abstract void loadAllDocumentation();

    protected void mapDocumentation(){
        for(ClassDocumentation documentation:classes.values()){
            documentation.getChildren().forEach(this::addIndirectChildren);
        }
        for(PackageDocumentation documentation:packages.values()){
            documentation.getChildren().forEach(this::addIndirectChildren);
        }
        for(FieldDocumentation documentation:fields.values()){
            documentation.getChildren().forEach(this::addIndirectChildren);
        }
        for(MethodDocumentation documentation:methods.values()){
            documentation.getChildren().forEach(this::addIndirectChildren);
        }
        for(ConstructorDocumentation documentation:constructors.values()){
            documentation.getChildren().forEach(this::addIndirectChildren);
        }
    }

    protected <E extends AnnotatedElement> void addIndirectChildren(AnnotatedElementDocumentation<E> element){
        if(element instanceof ClassDocumentation){
            classes.putIfAbsent((Class)element.getElement(),(ClassDocumentation) element);
            return;
        }
        if(element instanceof MethodDocumentation){
            methods.putIfAbsent((Method) element.getElement(),(MethodDocumentation) element);
            return;
        }
        if(element instanceof FieldDocumentation){
            fields.putIfAbsent((Field) element.getElement(),(FieldDocumentation) element);
            return;
        }
        if(element instanceof ConstructorDocumentation){
            constructors.putIfAbsent((Constructor) element.getElement(),(ConstructorDocumentation) element);
            return;
        }
        if(element instanceof PackageDocumentation){
            packages.putIfAbsent((Package)element.getElement(),(PackageDocumentation) element);
            return;
        }
        element.getChildren().forEach(this::addIndirectChildren);
    }

    protected void linkDocumentation(){
        for(Map.Entry<Package,PackageDocumentation> parent:packages.entrySet()){
            String parentName=parent.getKey().getName();
            for(Map.Entry<Package,PackageDocumentation> child:packages.entrySet()){
                String childParentName=parent.getKey().getName();
                int dot=childParentName.lastIndexOf('.');
                if(dot>=0) {
                    childParentName =childParentName.substring(0,dot);
                    if(parentName.equals(childParentName)){
                        child.getValue().setParent(parent.getValue());
                    }
                }
            }
        }
        for (Map.Entry<Class,ClassDocumentation> child:classes.entrySet()){
            Class enclosingC = child.getKey().getEnclosingClass();
            if(enclosingC!=null) {
                ClassDocumentation parentClass = classes.get(enclosingC);
                if (parentClass != null) {
                    parentClass.addChild(child.getValue());
                    continue;
                }
            }
            Method enclosingMethod = child.getKey().getEnclosingMethod();
            if(enclosingMethod!=null) {
                MethodDocumentation parentMethod = methods.get(enclosingMethod);
                if (parentMethod != null) {
                    parentMethod.addChild(child.getValue());
                    continue;
                }
            }
            Constructor enclosingConstructor = child.getKey().getEnclosingConstructor();
            if(enclosingConstructor!=null) {
                ConstructorDocumentation parentConstructor = constructors.get(enclosingConstructor);
                if (parentConstructor != null) {
                    parentConstructor.addChild(child.getValue());
                    continue;
                }
            }
            Package pack=child.getKey().getPackage();
            if(pack!=null) {
                PackageDocumentation parentPackage = packages.get(pack);
                if (parentPackage != null) {
                    parentPackage.addChild(child.getValue());
                }
            }
        }
        for(Map.Entry<Method,MethodDocumentation> child:methods.entrySet()){
            Class clazz=child.getKey().getDeclaringClass();
            if(clazz!=null) {
                ClassDocumentation parentClass = classes.get(clazz);
                if (parentClass != null) {
                    parentClass.addChild(child.getValue());
                }
            }
        }
        for(Map.Entry<Constructor,ConstructorDocumentation> child:constructors.entrySet()){
            Class clazz=child.getKey().getDeclaringClass();
            if(clazz!=null) {
                ClassDocumentation parentClass = classes.get(clazz);
                if (parentClass != null) {
                    parentClass.addChild(child.getValue());
                }
            }
        }
        for(Map.Entry<Field,FieldDocumentation> child:fields.entrySet()){
            Class clazz=child.getKey().getDeclaringClass();
            if(clazz!=null) {
                ClassDocumentation parentClass = classes.get(clazz);
                if (parentClass != null) {
                    parentClass.addChild(child.getValue());
                }
            }
        }
    }
}
