package java.com.github.technus.signalTester;

import com.bulenkov.darcula.DarculaLaf;

import javax.swing.*;
import java.awt.*;
import java.com.github.technus.signalTester.utility.Utility;
import java.io.File;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        try {
            SplashScreen splashScreen=SplashScreen.getSplashScreen();
            if(splashScreen!=null){
                Graphics2D graphics2D=splashScreen.createGraphics();
                if(graphics2D!=null){
                    graphics2D.setComposite(AlphaComposite.Clear);
                    //g.fillRect(130,250,280,40);
                    graphics2D.setPaintMode();
                    graphics2D.setColor(Color.CYAN);
                    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    graphics2D.setFont(new Font("Consolas",Font.BOLD,55));
                    FontMetrics metrics=graphics2D.getFontMetrics();
                    int lastY;
                    graphics2D.drawString("Signal Tester", metrics.getMaxAdvance(), lastY=metrics.getMaxAscent()+metrics.getMaxDescent());
                    if(args!=null && args[0] != null && !"_".equals(args[0])){
                        File f=new File(args[0]);
                        graphics2D.setFont(new Font("Consolas",Font.BOLD,30));
                        metrics=graphics2D.getFontMetrics();
                        graphics2D.drawString(f.getName().replaceFirst("\\.test\\.xml",""), metrics.getMaxAdvance(), lastY+metrics.getMaxAscent()+metrics.getMaxDescent()+metrics.getMaxDescent());
                    }
                    splashScreen.update();
                }
            }

            UIManager.setLookAndFeel(new DarculaLaf());
            Locale.setDefault(Locale.US);
        } catch (Exception e) {
            Utility.showThrowableMain(null,e,"Cannot load Look and Feel!");
            System.exit(0);
        }

        try{
            new Main();
        }catch (Throwable t){
            Utility.showThrowableMain(null,t,"Unhandled throwable!");
            System.exit(0);
        }
    }
}
