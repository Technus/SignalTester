package com.github.technus.signalTester.fx;

import com.github.technus.signalTester.SignalTesterHeadless;
import com.github.technus.signalTester.utility.Utility;
import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

public class SignalTester extends Application {
    private final SignalTesterHeadless signalTesterHeadless =new SignalTesterHeadless();

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root=FXMLLoader.load(getClass().getResource("Main.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    static {
        Application.setUserAgentStylesheet(STYLESHEET_MODENA);
        StyleManager.getInstance().addUserAgentStylesheet(SignalTester.class.getResource("modena_dark.css").toString());
    }

    public static void main(String... args) {
        Locale.setDefault(Locale.US);
        SignalTesterHeadless.showSplashScreen(args);
        try{
            new SignalTester();
        }catch (Throwable t){
            Utility.showThrowableMain(t,"Unhandled throwable!");
            System.exit(0);
        }
    }
}
