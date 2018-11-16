package com.github.technus.signalTester.fx.additions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

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

    public void initialize(){
        xMin.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.MIN_VALUE,Double.MAX_VALUE,10));
        xBase.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1+Double.MIN_NORMAL*2,Double.MAX_VALUE,10));
        xMinor.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,Integer.MAX_VALUE,10));
        xMajor.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0,Double.MAX_VALUE,10));
        xMax.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.MIN_VALUE,Double.MAX_VALUE,1000));

        yMin.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.MIN_VALUE,Double.MAX_VALUE,10));
        yBase.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1+Double.MIN_NORMAL*2,Double.MAX_VALUE,10));
        yMinor.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,Integer.MAX_VALUE,10));
        yMajor.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0,Double.MAX_VALUE,10));
        yMax.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.MIN_VALUE,Double.MAX_VALUE,1000));

        xAxis.setAutoRanging(false);
        xAuto.selectedProperty().bindBidirectional(xAxis.autoRangingProperty());
        xMin.getValueFactory().valueProperty().bindBidirectional(xAxis.logLowerBoundProperty().asObject());
        xMax.getValueFactory().valueProperty().bindBidirectional(xAxis.logUpperBoundProperty().asObject());
        xMinor.getValueFactory().valueProperty().bindBidirectional(xAxis.logMinorTickCountProperty().asObject());
        xMajor.getValueFactory().valueProperty().bindBidirectional(xAxis.logTickCountProperty().asObject());
        xBase.getValueFactory().valueProperty().bindBidirectional(xAxis.logBaseProperty().asObject());

        yAxis.setAutoRanging(false);
        yAuto.selectedProperty().bindBidirectional(yAxis.autoRangingProperty());
        yMin.getValueFactory().valueProperty().bindBidirectional(yAxis.logLowerBoundProperty().asObject());
        yMax.getValueFactory().valueProperty().bindBidirectional(yAxis.logUpperBoundProperty().asObject());
        yMinor.getValueFactory().valueProperty().bindBidirectional(yAxis.logMinorTickCountProperty().asObject());
        yMajor.getValueFactory().valueProperty().bindBidirectional(yAxis.logTickCountProperty().asObject());
        yBase.getValueFactory().valueProperty().bindBidirectional(yAxis.logBaseProperty().asObject());

        xCol.setCellValueFactory(new PropertyValueFactory<>("XValue"));
        xCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        yCol.setCellValueFactory(new PropertyValueFactory<>("YValue"));
        yCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        ObservableList<XYChart.Series<Double,Double>> seriesList=FXCollections.observableArrayList();
        chart.setData(seriesList);

        XYChart.Series<Double,Double> series=new XYChart.Series<>();
        table.setItems(series.getData());
        seriesList.add(series);
    }

    public void addPoint(){
        table.getItems().add(new XYChart.Data<>(1d,1d));
    }
}
