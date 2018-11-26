package com.github.technus.signalTester.test.data;

import com.github.technus.dbAdditions.mongoDB.pojo.Tuple3;
import javafx.scene.chart.XYChart;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.Instant;
import java.util.ArrayList;

public class AnalogSeries<E> extends Series<Double,Double,E> {
    @BsonCreator
    public AnalogSeries(@BsonProperty("timestamp") Instant timestamp,
                        @BsonProperty("data")ArrayList<Tuple3<Double,Double, E>> data,
                        @BsonProperty("name") String name,
                        @BsonProperty("nameX") String nameX,
                        @BsonProperty("nameY") String nameY,
                        @BsonProperty("unitX") String unitX,
                        @BsonProperty("unitY") String unitY){
        super(timestamp, data, name, nameX, nameY, unitX, unitY);
    }

    public AnalogSeries(XYChart.Series<Double,Double> series, String nameX, String nameY, String unitX, String unitY){
        super(series, nameX, nameY, unitX, unitY);
    }
}
