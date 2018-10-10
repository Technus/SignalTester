package com.github.technus.dbAdditions.mongoDB;

import com.github.technus.dbAdditions.mongoDB.conventions.RemovalConvention;
import com.mongodb.MongoClient;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonDocumentWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SafePOJO {
    public static List<Convention> CONVENTIONS = Arrays.asList(Conventions.CLASS_AND_PROPERTY_CONVENTION, Conventions.ANNOTATION_CONVENTION, RemovalConvention.INSTANCE);

    private SafePOJO(){
        MongoClient.getDefaultCodecRegistry();
    }

    /**
     * Builds really nice codec registry!!!
     * @param collectionType
     * @param defaultType
     * @param additionalDiscriminatedTypes
     * @param <T>
     * @return
     */
    @SafeVarargs
    public static <T> CodecRegistry buildCodecRegistry(Class<T> collectionType, Class<? extends T> defaultType, Class<? extends T>... additionalDiscriminatedTypes){
        return CodecRegistries.fromRegistries(
                MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(
                        buildCodecProvider(collectionType,defaultType,additionalDiscriminatedTypes)));
    }

    /**
     * Builds nice codec provider
     * @param collectionType
     * @param defaultType
     * @param additionalDiscriminatedTypes
     * @param <T>
     * @return
     */
    @SafeVarargs
    public static <T> CodecProvider buildCodecProvider(Class<T> collectionType, Class<? extends T> defaultType, Class<? extends T>... additionalDiscriminatedTypes){
        return getProviderBuilder(collectionType,defaultType,additionalDiscriminatedTypes).build();
    }

    /**
     * Builds nice codec provider builder, can register additional class models with discriminators!
     * @param collectionType
     * @param defaultType
     * @param additionalDiscriminatedTypes
     * @param <T>
     * @return
     */
    @SafeVarargs
    public static <T> PojoCodecProvider.Builder getProviderBuilder(Class<T> collectionType, Class<? extends T> defaultType, Class<? extends T>... additionalDiscriminatedTypes){
        PojoCodecProvider.Builder provider = PojoCodecProvider.builder()
                .conventions(CONVENTIONS)
                .register(buildClassInsteadOfDefault(defaultType,collectionType))
                .register(buildAsNullValueDiscriminatorConstructor(defaultType));
        for(Class<? extends T> clazz:additionalDiscriminatedTypes){
            provider.register(buildWithDiscriminator(clazz));
        }
        return provider;
    }

    /**
     * No discriminator!
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> ClassModel<T> buildAsNullValueDiscriminatorConstructor(Class<T> clazz){
        return ClassModel.builder(clazz)
                .conventions(CONVENTIONS)
                .enableDiscriminator(false)
                .build();//does work...
    }

    /**
     * With descriminator!
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> ClassModel<T> buildWithDiscriminator(Class<T> clazz){
        return ClassModel.builder(clazz)
                .conventions(CONVENTIONS)
                .enableDiscriminator(true)
                .discriminator(clazz.getName())
                .build();//does work...
    }

    /**
     * With discriminator!
     * @param clazz
     * @param discriminator
     * @param <T>
     * @return
     */
    public static <T> ClassModel<T> buildWithDiscriminator(Class<T> clazz, String discriminator){
        return ClassModel.builder(clazz)
                .conventions(CONVENTIONS)
                .enableDiscriminator(true)
                .discriminator(discriminator)
                .build();//does work...
    }

    /**
     * With discriminator!
     * @param clazz
     * @param discriminator
     * @param <T>
     * @return
     */
    public static <T> ClassModel<T> buildWithDiscriminator(Class<T> clazz, Class<T> discriminator){
        return ClassModel.builder(clazz)
                .conventions(CONVENTIONS)
                .enableDiscriminator(true)
                .discriminator(discriminator.getName())
                .build();//does work...
    }

    ///**
    // * Using oldType discriminator buildWithDiscriminator newType - does not work on thing below!
    // * @param newType
    // * @param oldType
    // * @param <T>
    // * @return
    // */
    //@Deprecated
    //@SuppressWarnings("unchecked")
    //private static <T> ClassModel<T> buildAs(Class<? extends T> newType, Class<? extends T> oldType){
    //    ClassModelBuilder builder= ClassModel.builder(newType);
    //    builder.build();
    //    return ClassModel.builder(newType)
    //            .instanceCreatorFactory(builder.getInstanceCreatorFactory())
    //            .type(oldType)
    //            .enableDiscriminator(true)
    //            .discriminator(oldType.getName())
    //            .build();
    //}

    /**
     * Used to setup the null and defaultClass discriminator behaviour to make clazz instances without changing the collection type
     * @param clazz
     * @param defaultClass
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> ClassModel<T> buildClassInsteadOfDefault(Class<? extends T> clazz, Class<T> defaultClass){
        ClassModelBuilder builder= ClassModel.builder(clazz)
                .conventions(CONVENTIONS)
                .enableDiscriminator(true);
        builder.build();
        ArrayList conventions=new ArrayList<>(builder.getConventions());
        conventions.add((Convention) classModelBuilder ->
                classModelBuilder.instanceCreatorFactory(builder.getInstanceCreatorFactory())
                        .type(defaultClass)
                        .enableDiscriminator(true));
        return ClassModel.builder(clazz).conventions(conventions).build();
    }

    ///**
    // * Used to setup the collection document type to be built as clazz instances without changing the collection type
    // * @param clazz
    // * @param collection
    // * @param <T>
    // * @return
    // */
    //@Deprecated
    //@SuppressWarnings("unchecked")
    //private static <T> ClassModel<T> buildClassInsteadOfDefault(Class<? extends T> clazz, MongoCollection<T> collection){
    //    ClassModelBuilder builder= ClassModel.builder(clazz);
    //    builder.build();
    //    return ClassModel.builder(clazz)
    //            .instanceCreatorFactory(builder.getInstanceCreatorFactory())
    //            .type(collection.getDocumentClass())
    //            .enableDiscriminator(true)
    //            .build();
    //}

    public static  <TDocument> BsonDocument encode(TDocument object, Class<TDocument> clazz, CodecRegistry codecRegistry){
        BsonDocument result=new BsonDocument();
        codecRegistry.get(clazz).encode(new BsonDocumentWriter(result),object,EncoderContext.builder().build());
        return result;
    }

    public static <TDocument> TDocument decode(BsonDocument bson, Class<TDocument> clazz, CodecRegistry codecRegistry){
        return codecRegistry.get(clazz).decode(new BsonDocumentReader(bson),DecoderContext.builder().build());
    }
}
