package com.github.technus.signalTester.fx.splash;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Splash {
    public Stage stage;
    public ProgressBar progressIndicator;
    public Label titleLabel;
    public Label subtitleLabel;
    public Label processLabel;

    public static Splash getInstance() throws IOException{
        return getInstance(Modality.APPLICATION_MODAL);
    }

    public static Splash getInstance(Modality modality) throws IOException{
        FXMLLoader loader = new FXMLLoader(Splash.class.getResource("SplashView.fxml"));
        Parent root= loader.load();
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.initModality(modality);
        stage.setScene(new Scene(root, 614, 461));

        Splash controller=loader.getController();
        controller.stage=stage;
        controller.stage.titleProperty().bindBidirectional(controller.titleLabel.textProperty());
        return controller;
    }

    public StringProperty titleProperty() {
        return titleLabel.textProperty();
    }

    public StringProperty subtitleProperty() {
        return subtitleLabel.textProperty();
    }

    public DoubleProperty progressProperty() {
        return progressIndicator.progressProperty();
    }

    public StringProperty messageProperty(){
        return processLabel.textProperty();
    }

    public void hide() {
        stage.hide();
    }

    public void show() {
        stage.show();
        stage.centerOnScreen();
        stage.getScene().getWindow().setY(stage.getScene().getWindow().getY() + 113);//TODO WHY???
    }
}
