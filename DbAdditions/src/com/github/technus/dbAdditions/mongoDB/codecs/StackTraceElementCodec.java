package com.github.technus.dbAdditions.mongoDB.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class StackTraceElementCodec implements Codec<StackTraceElement> {
    public static final Codec<StackTraceElement> INSTANCE=new StackTraceElementCodec();

    @Override
    public void encode(BsonWriter writer, StackTraceElement value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("declaringClass",value.getClassName());
        writer.writeString("methodName",value.getMethodName());
        writer.writeString("fileName",value.getFileName());
        writer.writeInt32("lineNumber",value.getLineNumber());
        writer.writeEndDocument();
    }

    @Override
    public StackTraceElement decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        StackTraceElement stackTraceElement= new StackTraceElement(
                reader.readString("declaringClass"),
                reader.readString("methodName"),
                reader.readString("fileName"),
                reader.readInt32("lineNumber")
        );
        reader.readEndDocument();
        return stackTraceElement;
    }

    @Override
    public Class<StackTraceElement> getEncoderClass() {
        return StackTraceElement.class;
    }
}
