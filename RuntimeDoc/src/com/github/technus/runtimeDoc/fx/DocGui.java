package com.github.technus.runtimeDoc.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class DocGui extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader=new FXMLLoader(DocumentationController.class.getResource("Documentation.fxml"),
                ResourceBundle.getBundle(DocumentationController.class.getName(),Locale.getDefault(),DocumentationController.class.getClassLoader()));
        Parent parent= loader.load();
        primaryStage.setScene(new Scene(parent));
        primaryStage.titleProperty().setValue("DocGUI");
        primaryStage.show();
    }
}
