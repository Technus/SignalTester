package com.github.technus.signalTester.test.data;

import com.github.technus.dbAdditions.mongoDB.pojo.Tuple3;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data analog value vs. analog value
 */
public class Series<X,Y, E> implements IData<X, Y, E> {
    private Instant timestamp;
    private String unitX,unitY,nameX,nameY;
    private XYChart.Series<X,Y> series;

    @BsonCreator
    public Series(@BsonProperty("timestamp") Instant timestamp,
                  @BsonProperty("data")ArrayList<Tuple3<X,Y,E>> data,
                  @BsonProperty("name") String name,
                  @BsonProperty("nameX") String nameX,
                  @BsonProperty("nameY") String nameY,
                  @BsonProperty("unitX") String unitX,
                  @BsonProperty("unitY") String unitY){
        this.timestamp = timestamp;
        this.nameX=nameX;
        this.nameY=nameY;
        this.unitX=unitX;
        this.unitY=unitY;
        this.series=new XYChart.Series<>();
        this.series.setName(name);
        ObservableList <XYChart.Data<X,Y>> list=FXCollections.observableArrayList();
        data.forEach(xyeTuple3 ->
                list.add(new XYChart.Data<>(xyeTuple3.getX(),xyeTuple3.getY(),xyeTuple3.getZ())));
        this.series.setData(list);
    }

    public Series(XYChart.Series<X,Y> series, String nameX, String nameY, String unitX, String unitY){
        this.timestamp =Instant.now();
        this.nameX=nameX;
        this.nameY=nameY;
        this.unitX=unitX;
        this.unitY=unitY;
        this.series=series;
    }

    @Override
    public XYChart.Series<X, Y> asSeries() {
        return series;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String getNameX() {
        return nameX;
    }

    @Override
    public String getNameY() {
        return nameY;
    }

    @Override
    public String getUnitX() {
        return unitX;
    }

    @Override
    public String getUnitY() {
        return unitY;
    }

    @Override
    public String getName(){
        return series.getName();
    }

    @Override
    public List<X> readDataX(){
        return series.getData().stream().map(XYChart.Data::getXValue).collect(Collectors.toList());
    }

    @Override
    public List<Y> readDataY(){
        return series.getData().stream().map(XYChart.Data::getYValue).collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<E> readDataE(){
        return (List<E>)series.getData().stream().map(XYChart.Data::getExtraValue).collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Tuple3<X, Y, E>> getData() {
        return series.getData().stream().map(xyData ->
                new Tuple3<>(xyData.getXValue(),xyData.getYValue(),(E)xyData.getExtraValue())).collect(Collectors.toList());
    }
}
