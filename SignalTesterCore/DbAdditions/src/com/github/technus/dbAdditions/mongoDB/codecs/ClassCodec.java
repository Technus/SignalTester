package com.github.technus.dbAdditions.mongoDB.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class ClassCodec implements Codec<Class> {
    public static final Codec<Class> INSTANCE=new ClassCodec();

    @Override
    public Class<Class> getEncoderClass() {
        return Class.class;
    }

    @Override
    public Class decode(BsonReader reader, DecoderContext decoderContext) {
        try {
            return Class.forName(reader.readString());
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public void encode(BsonWriter writer, Class value, EncoderContext encoderContext) {
        writer.writeString(value.getName());
    }
}
