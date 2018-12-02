package com.github.technus.signalTester.fx;

import com.github.technus.signalTester.fx.main.MainController;
import com.github.technus.signalTester.fx.main.MainModel;
import com.github.technus.signalTester.fx.splash.Splash;
import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.util.List;
import java.util.Locale;

import static com.github.technus.dbAdditions.mongoDB.fsBackend.FileSystemCollection.EXTENSION;

public class SignalTester extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader=new FXMLLoader(MainController.class.getResource("MainView.fxml"));
        Parent parent= loader.load();
        primaryStage.setScene(new Scene(parent));
        primaryStage.titleProperty().setValue("SignalTester");

        Splash splash=Splash.getInstance();
        splash.titleProperty().setValue("SignalTester");
        List<String> parameters=getParameters().getRaw();
        if (parameters != null && parameters.size()>0 && parameters.get(0).endsWith("."+EXTENSION)) {
            splash.subtitleProperty().setValue(parameters.get(0));
        } else {
            splash.subtitleProperty().setValue("Starting Application");
        }
        splash.stage.initOwner(primaryStage);

        Task<Void> task=new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (SplashScreen.getSplashScreen() != null) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {}
                    SplashScreen.getSplashScreen().close();
                }
                int workAmount=4,workDone=0;

                updateMessage("Loading main controller");
                updateProgress(workDone++,workAmount);
                MainController controller=loader.getController();
                updateProgress(workDone++,workAmount);
                controller.model=new MainModel(parameters);
                updateProgress(workDone++,workAmount);
                Thread.setDefaultUncaughtExceptionHandler((t,e)->{
                    controller.model.showThrowableMain(e);
                    //if(!(e instanceof Exception)){
                        Platform.exit();
                    //}
                });
                updateProgress(workDone++,workAmount);
                controller.model.initialize();
                updateProgress(workDone++,workAmount);

                updateMessage("Loading finished");
                updateProgress(-1,-1);
                Thread.sleep(2000);
                Platform.runLater(()-> {
                    primaryStage.show();
                    splash.hide();
                });
                return null;
            }
        };
        splash.messageProperty().setValue("Preparing");
        splash.progressProperty().bind(task.progressProperty());
        splash.messageProperty().bind(task.messageProperty());

        splash.show();
        new Thread(task).start();
    }

    public static void main(String... args) {
        Locale.setDefault(Locale.US);
        try {
            setUserAgentStylesheet(STYLESHEET_MODENA);
            StyleManager.getInstance().addUserAgentStylesheet(SignalTester.class.getResource("modena_dark.css").toString());
            launch(SignalTester.class,args);
        } catch (Exception e) {
            e.printStackTrace();
            Platform.exit();
        }
    }
}
