package com.github.technus.signalTester.fx.additions;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LogTestApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader=new FXMLLoader(Logtest.class.getResource("logtest.fxml"));
        Parent parent= loader.load();
        primaryStage.setScene(new Scene(parent));
        primaryStage.titleProperty().setValue("LogTest");
        primaryStage.show();
    }
}
