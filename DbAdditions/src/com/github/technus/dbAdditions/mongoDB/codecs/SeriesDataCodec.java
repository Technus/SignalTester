package com.github.technus.dbAdditions.mongoDB.codecs;

import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

public class SeriesDataCodec implements Codec<XYChart.Series> {
    private final CodecRegistry registry;

    public SeriesDataCodec(CodecRegistry dataCodecs){
        this.registry=dataCodecs;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void encode(BsonWriter writer, XYChart.Series value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("name",value.getName());
        writer.writeStartArray("data");
        for (Object data: value.getData()) {
            Codec dataCodec = registry.get(data.getClass());
            encoderContext.encodeWithChildContext(dataCodec, writer, data);
        }
        writer.writeEndArray();
        writer.writeEndDocument();
    }

    @Override
    @SuppressWarnings("unchecked")
    public XYChart.Series decode(BsonReader reader, DecoderContext decoderContext) {
        XYChart.Series series=new XYChart.Series<>();
        ObservableList<XYChart.Data> dataList=series.getData();
        reader.readStartDocument();
        series.setName(reader.readString("name"));
        reader.readName("data");
        reader.readStartArray();
        while (reader.readBsonType() == BsonType.DOCUMENT) {
            Codec<XYChart.Data> codec=registry.get(XYChart.Data.class);
            XYChart.Data data=decoderContext.decodeWithChildContext(codec,reader);
            dataList.add(data);
        }
        reader.readEndArray();
        reader.readEndDocument();
        return series;
    }

    @Override
    public Class<XYChart.Series> getEncoderClass() {
        return XYChart.Series.class;
    }
}
