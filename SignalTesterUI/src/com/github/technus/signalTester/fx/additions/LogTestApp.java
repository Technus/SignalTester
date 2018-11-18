package com.github.technus.signalTester.fx.additions;

import com.github.technus.signalTester.fx.SignalTester;
import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LogTestApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        setUserAgentStylesheet(STYLESHEET_MODENA);
        StyleManager.getInstance().addUserAgentStylesheet(SignalTester.class.getResource("modena_dark.css").toString());
        FXMLLoader loader=new FXMLLoader(Logtest.class.getResource("logtest.fxml"));
        Parent parent= loader.load();
        primaryStage.setScene(new Scene(parent));
        primaryStage.titleProperty().setValue("LogTest");
        primaryStage.show();
    }
}
