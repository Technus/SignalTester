package com.github.technus.dbAdditions.mongoDB.codecs;

import javafx.scene.chart.XYChart;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

public class DataContentCodec<X,Y,E> implements Codec<XYChart.Data<X,Y>> {
    private final Class<X> xClass;
    private final Class<Y> yClass;
    private final Class<E> extraClass;
    private final CodecRegistry registry;

    public DataContentCodec(Class<X> xClass, Class<Y> yClass, Class<E> extraClass, CodecRegistry contentCodecs){
        this.extraClass=extraClass;
        this.xClass =xClass;
        this.yClass =yClass;
        this.registry=contentCodecs;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void encode(BsonWriter writer, XYChart.Data<X, Y> value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeName("extra");
        if (value.getExtraValue() == null) {
            writer.writeNull();
        } else {
            Codec codec = registry.get(value.getExtraValue().getClass());
            encoderContext.encodeWithChildContext(codec, writer, value.getExtraValue());
        }

        writer.writeName("x");
        if (value.getXValue() == null) {
            writer.writeNull();
        } else {
            Codec codec = registry.get(value.getXValue().getClass());
            encoderContext.encodeWithChildContext(codec, writer, value.getXValue());
        }

        writer.writeName("y");
        if (value.getYValue() == null) {
            writer.writeNull();
        } else {
            Codec codec = registry.get(value.getYValue().getClass());
            encoderContext.encodeWithChildContext(codec, writer, value.getYValue());
        }
        writer.writeEndDocument();
    }

    @Override
    @SuppressWarnings("unchecked")
    public XYChart.Data<X, Y> decode(BsonReader reader, DecoderContext decoderContext) {
        XYChart.Data data=new XYChart.Data();
        reader.readStartDocument();
        reader.readName("extra");
        if(reader.getCurrentBsonType()!= BsonType.NULL){
            Codec codec=registry.get(extraClass);
            data.setExtraValue(decoderContext.decodeWithChildContext(codec,reader));
        }else{
            reader.readNull();
        }
        reader.readName("x");
        if(reader.getCurrentBsonType()!= BsonType.NULL){
            Codec codec=registry.get(xClass);
            data.setXValue(decoderContext.decodeWithChildContext(codec,reader));
        }else{
            reader.readNull();
        }
        reader.readName("y");
        if(reader.getCurrentBsonType()!= BsonType.NULL){
            Codec codec=registry.get(yClass);
            data.setYValue(decoderContext.decodeWithChildContext(codec,reader));
        }else{
            reader.readNull();
        }
        reader.readEndDocument();
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<XYChart.Data<X, Y>> getEncoderClass() {
        return (Class) XYChart.Data.class;
    }
}
