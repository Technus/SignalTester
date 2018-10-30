package com.github.technus.signalTester.fx;

import com.github.technus.dbAdditions.mongoDB.fsBackend.FileSystemCollection;
import com.github.technus.signalTester.fx.main.MainWindow;
import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.Locale;

public class SignalTester extends Application {
    public static String[] parameters;

    @Override
    public void start(Stage primaryStage) {
        parameters=getParameters().getRaw().toArray(new String[0]);

        Parent finalParent,root=null;
        try {
            root = FXMLLoader.load(MainWindow.class.getResource("MainWindow.fxml"));
        }catch (IOException e){
            throw new IOError(e);
        }
        if(root!=null) {
            finalParent = root;
                primaryStage.setScene(new Scene(finalParent));
                primaryStage.setTitle("SignalTester");
                primaryStage.show();
                if(SplashScreen.getSplashScreen()!=null) {
                    SplashScreen.getSplashScreen().close();
                }
        }
    }

    public static void main(String... args) {
        Locale.setDefault(Locale.US);
        try {
            SplashScreen splashScreen = SplashScreen.getSplashScreen();
            if (splashScreen != null) {
                Graphics2D graphics2D = splashScreen.createGraphics();
                if (graphics2D != null) {
                    graphics2D.setComposite(AlphaComposite.Clear);
                    graphics2D.setPaintMode();
                    graphics2D.setColor(Color.CYAN);
                    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    graphics2D.setFont(new Font("Consolas", Font.BOLD, 55));
                    FontMetrics metrics = graphics2D.getFontMetrics();
                    int lastY, lastX;
                    graphics2D.drawString("SignalTester", lastX = metrics.getMaxAdvance(),
                            lastY = metrics.getMaxAscent() + metrics.getMaxDescent());

                    graphics2D.setFont(new Font("Consolas", Font.BOLD, 30));
                    metrics = graphics2D.getFontMetrics();
                    String msg = "Starting appplication";
                    if (args != null && args.length > 0 && args[0] != null && args[0].length() > 0) {
                        File f = new File(args[0]);
                        if (f.isFile()) {
                            msg = f.getName().replaceFirst("\\." + FileSystemCollection.EXTENSION, "");
                        }
                    }
                    graphics2D.drawString(msg, lastX,
                            lastY + metrics.getMaxAscent() + metrics.getMaxDescent() + metrics.getMaxDescent());
                    splashScreen.update();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            setUserAgentStylesheet(STYLESHEET_MODENA);
            StyleManager.getInstance().addUserAgentStylesheet(SignalTester.class.getResource("modena_dark.css").toString());
            launch(SignalTester.class,args);
        } catch (Exception t) {
            t.printStackTrace();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ignored) {}
            Platform.exit();
        }
    }
}
