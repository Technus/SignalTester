package com.github.technus.signalTester.fx.main;

import com.github.technus.signalTester.SignalTesterHeadless;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

public class MainWindow {
    private final SignalTesterHeadless signalTesterHeadless;

    public MainWindow(){
        signalTesterHeadless=new SignalTesterHeadless();
        showThrowableMain(new Exception("U ZUCC"));
    }

    public AnchorPane rootPane;
    public BorderPane mainPane;

    public Region defaultRegion;

    public ButtonType showConfirmThrowableMain(Throwable throwable,ButtonType... buttonTypes){
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

    private ButtonType showConfirmThrowable(Region component,Throwable throwable,ButtonType... buttonTypes){
        signalTesterHeadless.logError(throwable);
        Alert alert = new Alert(Alert.AlertType.ERROR,null,buttonTypes);
        alert.setTitle(throwable.getClass().getSimpleName());
        alert.setHeaderText(throwable.getLocalizedMessage());
        ScrollPane scrollPane=new ScrollPane();
        Label label=new Label(printThrowable(throwable));
        scrollPane.setContent(label);
        alert.getDialogPane().setContent(scrollPane);
        alert.setResizable(true);
        Optional<ButtonType> buttonTypeOptional=alert.showAndWait();
        return buttonTypeOptional.orElse(ButtonType.CLOSE);
    }

    private void showThrowable(Region component,Throwable throwable){
        showConfirmThrowable(component,throwable);
    }

    private JScrollPane scrollThrowable(Throwable t){
        return scrollable(printThrowable(t));
    }

    private static JScrollPane scrollable(String t){
        JTextArea area=new JTextArea();
        area.setEditable(false);
        area.setText(t);
        JScrollPane pane=new JScrollPane(area);
        pane.setPreferredSize(new Dimension(700,500));
        return pane;
    }

    private static String printThrowable(Throwable t){
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        PrintStream printStream=new PrintStream(outputStream);
        t.printStackTrace(printStream);
        t.printStackTrace();
        try {
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
        return new String(outputStream.toByteArray());
    }
}
