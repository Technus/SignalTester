package com.github.technus.signalTester.fx.main;

import com.github.technus.signalTester.SignalTesterHeadless;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class MainWindow {
    private final SignalTesterHeadless signalTesterHeadless;

    public MainWindow(){
        signalTesterHeadless=new SignalTesterHeadless();
    }

    public BorderPane mainPane;

    public Region defaultRegion;

    public int showConfirmThrowableMain(Throwable throwable,int option){
        return showConfirmThrowable(defaultRegion,throwable,option);
    }

    public void showThrowableMain(Throwable throwable){
        showThrowable(defaultRegion,throwable);
    }

    public int showConfirmThrowableMain(Region component, Throwable throwable, int option){
        return showConfirmThrowable(component==null?defaultRegion:component,throwable,option);
    }

    public void showThrowableMain(Region component,Throwable throwable){
        showThrowable(component==null?defaultRegion:component,throwable);
    }

    private int showConfirmThrowable(Region component,Throwable throwable,int option){
        signalTesterHeadless.logError(throwable);
        return JOptionPane.showConfirmDialog(null,scrollThrowable(throwable),throwable.getClass().getSimpleName(),option);
    }

    private void showThrowable(Region component,Throwable throwable){
        signalTesterHeadless.logError(throwable);
        JOptionPane.showMessageDialog(null,scrollThrowable(throwable),throwable.getClass().getSimpleName(),JOptionPane.ERROR_MESSAGE);
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
