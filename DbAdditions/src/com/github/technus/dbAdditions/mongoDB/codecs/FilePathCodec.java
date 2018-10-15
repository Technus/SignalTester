package com.github.technus.dbAdditions.mongoDB.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.io.File;

public class FilePathCodec implements Codec<File> {
    public static final Codec<File> INSTANCE=new FilePathCodec();

    @Override
    public Class<File> getEncoderClass() {
        return File.class;
    }

    @Override
    public File decode(BsonReader reader, DecoderContext decoderContext) {
        return new File(reader.readString());
    }

    @Override
    public void encode(BsonWriter writer, File value, EncoderContext encoderContext) {
        writer.writeString(value.getAbsolutePath());
    }
}
