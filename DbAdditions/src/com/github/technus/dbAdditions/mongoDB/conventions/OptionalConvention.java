package com.github.technus.dbAdditions.mongoDB.conventions;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.pojo.ClassModelBuilder;
import org.bson.codecs.pojo.Convention;
import org.bson.codecs.pojo.PropertyModelBuilder;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Locale;

public class OptionalConvention implements Convention {
    public static OptionalConvention INSTANCE=new OptionalConvention();

    @Override
    public void apply(ClassModelBuilder<?> classModelBuilder) {
        for (Annotation a:classModelBuilder.getAnnotations()) {
            if(a instanceof BsonOptional){
                if(((BsonOptional) a).value().equals("")){
                    for(PropertyModelBuilder propertyModelBuilder:classModelBuilder.getPropertyModelBuilders()){
                        makeOptional(propertyModelBuilder);
                    }
                }else {
                    PropertyModelBuilder propertyModelBuilder=classModelBuilder.getProperty(((BsonOptional) a).value());
                    makeOptional(propertyModelBuilder);
                }
            }else if (a instanceof BsonOptionals){
                for(BsonOptional b:((BsonOptionals) a).value()){
                    if(b.value().equals("")){
                        System.out.println("Cannot use default value for BsonOptionals at TYPE "+classModelBuilder.getType().getName());
                    }else {
                        PropertyModelBuilder propertyModelBuilder=classModelBuilder.getProperty(b.value());
                        makeOptional(propertyModelBuilder);
                    }
                }
            }
        }

        Class clazz=classModelBuilder.getType();

        for(Field field:clazz.getFields()){
            if(!Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())){
                for(Annotation a:field.getAnnotations()){
                    if(a instanceof BsonOptional){
                        if(((BsonOptional) a).value().equals("")){
                            PropertyModelBuilder propertyModelBuilder=classModelBuilder.getProperty(field.getName());
                            makeOptional(propertyModelBuilder);
                        }else {
                            PropertyModelBuilder propertyModelBuilder=classModelBuilder.getProperty(((BsonOptional) a).value());
                            makeOptional(propertyModelBuilder);
                        }
                    }
                }
            }
        }

        for(Method method:clazz.getMethods()){
            if(Modifier.isPublic(method.getModifiers())){
                for(Annotation a:method.getAnnotations()){
                    if(a instanceof BsonOptional){
                        if(((BsonOptional) a).value().equals("")){
                            PropertyModelBuilder propertyModelBuilder=classModelBuilder.getProperty(getPropertyName(method.getName()));
                            makeOptional(propertyModelBuilder);
                        }else {
                            PropertyModelBuilder propertyModelBuilder=classModelBuilder.getProperty(((BsonOptional) a).value());
                            makeOptional(propertyModelBuilder);
                        }
                    }
                }
            }
        }

        for(Constructor constructor:clazz.getConstructors()){
            if(Modifier.isPublic(constructor.getModifiers())){
                for(Annotation a:constructor.getAnnotations()){
                    if(a instanceof BsonOptional){
                        if(((BsonOptional) a).value().equals("")){
                            PropertyModelBuilder propertyModelBuilder=classModelBuilder.getProperty(getPropertyName(constructor.getName()));
                            makeOptional(propertyModelBuilder);
                        }else {
                            PropertyModelBuilder propertyModelBuilder=classModelBuilder.getProperty(((BsonOptional) a).value());
                            makeOptional(propertyModelBuilder);
                        }
                    }
                }
            }
            for(Parameter parameter:constructor.getParameters()){
                String name="";
                for(Annotation a:parameter.getAnnotations()){
                    if(a instanceof BsonProperty){
                        if(!((BsonProperty) a).value().equals("")){
                            name=((BsonProperty) a).value();
                        }
                    }
                }
                for(Annotation a:parameter.getAnnotations()){
                    if(a instanceof BsonOptional){
                        if(((BsonOptional) a).value().equals("")){
                            PropertyModelBuilder propertyModelBuilder=classModelBuilder.getProperty(getPropertyName(name.equals("")?parameter.getName():name));
                            makeOptional(propertyModelBuilder);
                        } else {
                            PropertyModelBuilder propertyModelBuilder=classModelBuilder.getProperty(((BsonOptional) a).value());
                            makeOptional(propertyModelBuilder);
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

    @SuppressWarnings("unchecked")
    private void makeOptional(PropertyModelBuilder propertyModelBuilder){
        if(propertyModelBuilder!=null){
            Codec codec=propertyModelBuilder.build().getCodec();
            if(codec==null){
                throw new Error("Cannot apply since codec is null: "+propertyModelBuilder.getName());
            }
            Codec optionalCodec=new Codec() {
                @Override
                public Object decode(BsonReader bsonReader, DecoderContext decoderContext) {
                    //try {
                        return codec.decode(bsonReader, decoderContext);
                    //}catch (Exception e){
                    //    return null;
                    //}
                }

                @Override
                public void encode(BsonWriter bsonWriter, Object o, EncoderContext encoderContext) {
                    codec.encode(bsonWriter, o, encoderContext);
                }

                @Override
                public Class getEncoderClass() {
                    return codec.getEncoderClass();
                }
            };
            propertyModelBuilder.codec(optionalCodec);
        }
    }
}
