package com.github.technus.signalTester.test.data;

import com.github.technus.dbAdditions.mongoDB.pojo.Tuple3;
import javafx.scene.chart.XYChart;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.Instant;
import java.util.ArrayList;

public class DiscreteSeries<E> extends Series<Double,Long,E> {
    @BsonCreator
    public DiscreteSeries(@BsonProperty("timestamp") Instant timestamp,
                          @BsonProperty("data")ArrayList<Tuple3<Double,Long, E>> data,
                          @BsonProperty("name") String name,
                          @BsonProperty("nameX") String nameX,
                          @BsonProperty("nameY") String nameY,
                          @BsonProperty("unitX") String unitX,
                          @BsonProperty("unitY") String unitY){
        super(timestamp, data, name, nameX, nameY, unitX, unitY);
    }

    public DiscreteSeries(XYChart.Series<Double,Long> series, String nameX, String nameY, String unitX, String unitY){
        super(series, nameX, nameY, unitX, unitY);
    }
}
