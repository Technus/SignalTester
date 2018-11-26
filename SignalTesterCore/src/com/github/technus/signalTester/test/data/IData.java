package com.github.technus.signalTester.test.data;

import com.github.technus.dbAdditions.functionalInterfaces.ITimedModification;
import com.github.technus.dbAdditions.mongoDB.conventions.BsonTypeRemove;
import com.github.technus.dbAdditions.mongoDB.pojo.Tuple3;
import javafx.scene.chart.XYChart;

import java.util.List;

@BsonTypeRemove(XYChart.Series.class)
public interface IData<X,Y,E> extends ITimedModification {
    List<Tuple3<X,Y,E>> getData();
    String getName();
    String getNameX();
    String getNameY();
    String getUnitX();
    String getUnitY();
    List<X> readDataX();
    List<Y> readDataY();
    List<E> readDataE();
    XYChart.Series<X,Y> asSeries();
}
