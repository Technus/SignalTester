package com.github.technus.signalTester.fx.additions;

import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

import java.util.ArrayList;
import java.util.Random;

public class Logtest {
    public LineChart<Double,Double> chart;
    public TableColumn<XYChart.Data<Double,Double>,Double> xCol;
    public Spinner<Double> xMin;
    public Spinner<Double> xBase;
    public Spinner<Double> xMajor;
    public Spinner<Integer> xMinor;
    public Spinner<Double> xMax;
    public CheckBox xAuto;
    public LogLinAxis xAxis;

    public TableColumn<XYChart.Data<Double,Double>,Double> yCol;
    public Spinner<Double> yMin;
    public Spinner<Double> yBase;
    public Spinner<Double> yMajor;
    public Spinner<Integer> yMinor;
    public Spinner<Double> yMax;
    public CheckBox yAuto;
    public LogLinAxis yAxis;

    public TableView<XYChart.Data<Double,Double>> table;

    public ArrayList<Property> weakBinds=new ArrayList<Property>(){
        @Override
        public Property set(int index, Property element) {
            if(index<0){
                add(element);
                return element;
            }
            return super.set(index, element);
        }
    };

    @SuppressWarnings("unchecked")
    public void initialize(){
        xMin.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.MIN_VALUE,Double.MAX_VALUE,10,.1));
        xMin.getValueFactory().converterProperty().set(new DoubleStringConverter());
        xBase.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0+Double.MIN_NORMAL*2,Double.MAX_VALUE,10));
        xMinor.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,Integer.MAX_VALUE,10));
        xMajor.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0,Double.MAX_VALUE,10));
        xMax.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.MIN_VALUE,Double.MAX_VALUE,1000));

        yMin.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.MIN_VALUE,Double.MAX_VALUE,10));
        yBase.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1+Double.MIN_NORMAL*2,Double.MAX_VALUE,10));
        yMinor.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,Integer.MAX_VALUE,10));
        yMajor.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0,Double.MAX_VALUE,10));
        yMax.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.MIN_VALUE,Double.MAX_VALUE,1000));

        xAxis.setAutoRanging(false);
        xAuto.selectedProperty().bindBidirectional(weakBinds.set(-1,xAxis.autoRangingProperty()));
        xMin.getValueFactory().valueProperty().bindBidirectional(weakBinds.set(-1,xAxis.logLowerBoundProperty().asObject()));
        xMax.getValueFactory().valueProperty().bindBidirectional(weakBinds.set(-1,xAxis.logUpperBoundProperty().asObject()));
        xMinor.getValueFactory().valueProperty().bindBidirectional(weakBinds.set(-1,xAxis.logMinorTickCountProperty().asObject()));
        xMajor.getValueFactory().valueProperty().bindBidirectional(weakBinds.set(-1,xAxis.logTickCountProperty().asObject()));
        xBase.getValueFactory().valueProperty().bindBidirectional(weakBinds.set(-1,xAxis.logBaseProperty().asObject()));

        yAxis.setAutoRanging(false);
        yAuto.selectedProperty().bindBidirectional(weakBinds.set(-1,yAxis.autoRangingProperty()));
        yMin.getValueFactory().valueProperty().bindBidirectional(weakBinds.set(-1,yAxis.logLowerBoundProperty().asObject()));
        yMax.getValueFactory().valueProperty().bindBidirectional(weakBinds.set(-1,yAxis.logUpperBoundProperty().asObject()));
        yMinor.getValueFactory().valueProperty().bindBidirectional(weakBinds.set(-1,yAxis.logMinorTickCountProperty().asObject()));
        yMajor.getValueFactory().valueProperty().bindBidirectional(weakBinds.set(-1,yAxis.logTickCountProperty().asObject()));
        yBase.getValueFactory().valueProperty().bindBidirectional(weakBinds.set(-1,yAxis.logBaseProperty().asObject()));

        xCol.setCellValueFactory(new PropertyValueFactory<>("XValue"));
        xCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        yCol.setCellValueFactory(new PropertyValueFactory<>("YValue"));
        yCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        ObservableList<XYChart.Series<Double,Double>> seriesList=FXCollections.observableArrayList();

        XYChart.Series<Double,Double> series=new XYChart.Series<>();
        series.setName("Table Data");
        table.setItems(series.getData());
        seriesList.add(series);

        XYChart.Series<Double,Double> series1=new XYChart.Series<>();
        series1.setName("Linear");
        seriesList.add(series1);

        for(int i=0;i<20;i++){
            addPoint();
            series1.getData().add(new XYChart.Data<>((1d*i+1)/100,(1d*i+1)/10000));
        }

        chart.setData(seriesList);
    }

    private static final Random r=new Random();
    public void addPoint(){
        double d=r.nextDouble()*100+1;
        table.getItems().add(new XYChart.Data<>(d, Math.log10(d)));
    }
}
