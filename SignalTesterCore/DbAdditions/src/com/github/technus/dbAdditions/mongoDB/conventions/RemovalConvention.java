package com.github.technus.dbAdditions.mongoDB.conventions;

import org.bson.codecs.pojo.ClassModelBuilder;
import org.bson.codecs.pojo.Convention;
import org.bson.codecs.pojo.PropertyModelBuilder;
import org.bson.codecs.pojo.TypeWithTypeParameters;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Locale;

public class RemovalConvention implements Convention {
    public static RemovalConvention INSTANCE=new RemovalConvention();

    @Override
    public void apply(ClassModelBuilder<?> classModelBuilder) {
        for (Annotation a:classModelBuilder.getAnnotations()) {
            if(a instanceof BsonRemove) {
                if(((BsonRemove) a).value().equals("")){
                    System.out.println("Cannot use default value for BsonRemove at TYPE "+classModelBuilder.getType().getName());
                }else {
                    classModelBuilder.removeProperty(((BsonRemove) a).value());
                }
            }else if(a instanceof BsonRemovers){
                for (BsonRemove remove :((BsonRemovers) a).value()) {
                    if(remove.value().equals("")){
                        System.out.println("Cannot use default value for BsonRemove at TYPE "+classModelBuilder.getType().getName());
                    }else {
                        classModelBuilder.removeProperty(remove.value());
                    }
                }
            }else if (a instanceof  BsonTypeRemove){
                HashSet<String> namesToRemove=new HashSet<>();
                for(PropertyModelBuilder builder:classModelBuilder.getPropertyModelBuilders()){
                    if((builder.isReadable()||builder.isWritable()) && ((TypeWithTypeParameters)builder.build().getTypeData()).getType() == ((BsonTypeRemove) a).value()){
                        namesToRemove.add(builder.getName());
                    }
                }
                for (String name : namesToRemove) {
                    classModelBuilder.removeProperty(name);
                }
            }else if (a instanceof BsonTypeRemovers){
                HashSet<String> namesToRemove=new HashSet<>();
                for(BsonTypeRemove remove:((BsonTypeRemovers) a).value()){
                    for(PropertyModelBuilder builder:classModelBuilder.getPropertyModelBuilders()){
                        if((builder.isReadable()||builder.isWritable()) && ((TypeWithTypeParameters)builder.build().getTypeData()).getType() == remove.value()){
                            namesToRemove.add(builder.getName());
                        }
                    }
                }
                for (String name : namesToRemove) {
                    classModelBuilder.removeProperty(name);
                }
            }
        }

        Class clazz=classModelBuilder.getType();

        for(Field field:clazz.getFields()){
            if(!Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())){
                for(Annotation a:field.getAnnotations()){
                    if(a instanceof BsonRemove) {
                        if(((BsonRemove) a).value().equals("")){
                            classModelBuilder.removeProperty(field.getName());
                        }else {
                            classModelBuilder.removeProperty(((BsonRemove) a).value());
                        }
                    }
                }
            }
        }

        for(Method method:clazz.getMethods()){
            if(!Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers())){
                for(Annotation a:method.getAnnotations()){
                    if(a instanceof BsonRemove) {
                        if(((BsonRemove) a).value().equals("")){
                            classModelBuilder.removeProperty(getPropertyName(method.getName()));
                        }else {
                            classModelBuilder.removeProperty(((BsonRemove) a).value());
                        }
                    }
                }
            }
        }
    }

    private String getPropertyName(String name){
        name=name.replaceAll("^(is)|(get)|(set)","");
        return name.substring(0,1).toLowerCase(Locale.ENGLISH)+name.substring(1);
    }
}
