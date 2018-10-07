package java.com.github.technus.signalTester.utility;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.swing.*;

@Deprecated
public class VolumeControl extends JPanel {
    private VolumeControl(){}

    private static FloatControl getVolumeControl(){
        try {
            Mixer.Info mixers[] = AudioSystem.getMixerInfo();
            for (Mixer.Info mixerInfo : mixers) {
                Mixer mixer = AudioSystem.getMixer(mixerInfo);
                mixer.open();

                //we check only target type lines, because we are looking for "SPEAKER target port"
                for (Line.Info info : mixer.getTargetLineInfo()) {
                    if (info.toString().contains("SPEAKER")) {
                        Line line = mixer.getLine(info);
                        try {
                            line.open();
                        } catch (IllegalArgumentException ignored) {}
                        return (FloatControl) line.getControl(FloatControl.Type.VOLUME);
                    }
                }
            }
        } catch (Exception ex) {
            Utility.showThrowableMain(null,ex,"Cannot open java sound slider");
        }
        Utility.showThrowableMain(null,new Exception("Java sound slider not found"),"Java sound slider not found");
        return null;
    }

    private static final FloatControl volControl=getVolumeControl();

    public static void muteVolume() throws Exception{
        getVolumeControl().setValue(0f);
    }

    public static void maxVolume() throws Exception{
        getVolumeControl().setValue(1f);
    }

    public static void setVolumeFloat(float volume) throws Exception{
        getVolumeControl().setValue(volume);
    }

    public static void setVolumePercent(float volume) throws Exception{
        getVolumeControl().setValue(volume*100f);
    }

    public static void setVolumeInt(int volume) throws Exception{
        getVolumeControl().setValue(volume/65535f);
    }
}
