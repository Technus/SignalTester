package com.github.technus.signalTester.fx.main;

import com.github.technus.signalTester.SignalTesterHeadless;
import com.github.technus.signalTester.Utility;
import javafx.geometry.Bounds;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;

import java.util.List;
import java.util.Optional;

public class MainModel {
    public final SignalTesterHeadless headless;

    public Region defaultRegion;//default place for error log

    public MainModel(List<String> parameters){
        headless=new SignalTesterHeadless(parameters);
    }

    public void initialize(){
        headless.initialize();
    }

    //region throwable gui
    public ButtonType showConfirmThrowableMain(Throwable throwable, ButtonType... buttonTypes){
        return showConfirmThrowable(defaultRegion,throwable,buttonTypes);
    }

    public void showThrowableMain(Throwable throwable){
        showThrowable(defaultRegion,throwable);
    }

    public ButtonType showConfirmThrowableMain(Region component, Throwable throwable, ButtonType... buttonTypes){
        return showConfirmThrowable(component==null?defaultRegion:component,throwable,buttonTypes);
    }

    public void showThrowableMain(Region component,Throwable throwable){
        showThrowable(component==null?defaultRegion:component,throwable);
    }

    private void showThrowable(Region component,Throwable throwable){
        showConfirmThrowable(component,throwable);
    }

    private ButtonType showConfirmThrowable(Region region,Throwable throwable,ButtonType... buttonTypes){
        headless.logError(throwable);
        Alert.AlertType type=throwable instanceof Exception? Alert.AlertType.WARNING: Alert.AlertType.ERROR;
        Alert alert = new Alert(type,null,buttonTypes);
        alert.setTitle(throwable.getClass().getSimpleName());
        alert.setHeaderText(throwable.getLocalizedMessage());
        ScrollPane scrollPane=new ScrollPane();
        Label label=new Label(Utility.throwableToString(throwable));
        scrollPane.setContent(label);
        alert.getDialogPane().setContent(scrollPane);
        alert.setResizable(true);
        if(region!=null) {
            Bounds bounds = region.localToScreen(region.getBoundsInLocal());
            if(bounds!=null) {
                alert.setX(bounds.getMinX() + (region.getWidth() - alert.getWidth()) / 2D);
                alert.setY(bounds.getMinY() + (region.getHeight() - alert.getHeight()) / 2D);
            }
        }
        Optional<ButtonType> buttonTypeOptional=alert.showAndWait();
        return buttonTypeOptional.orElse(ButtonType.CLOSE);
    }
    //endregion
}
