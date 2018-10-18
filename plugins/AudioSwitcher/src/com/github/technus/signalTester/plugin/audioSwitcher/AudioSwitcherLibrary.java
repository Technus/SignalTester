package com.github.technus.signalTester.plugin.audioSwitcher;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.PointerByReference;

public interface AudioSwitcherLibrary extends Library {
    String JNA_LIBRARY_NAME ="";// SignalTesterHeadless.LIB_LOCATION.getAbsolutePath()+File.separator+ Platform.RESOURCE_PREFIX+ File.separator+"AudioDefaultSwitcher.dll";
    //NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(AudioSwitcherLibrary.JNA_LIBRARY_NAME);
    AudioSwitcherLibrary INSTANCE = Native.load(AudioSwitcherLibrary.JNA_LIBRARY_NAME, AudioSwitcherLibrary.class);

    boolean switch_to(WString deviceId, int DeviceRole);

    Pointer get_instance();

    boolean is_default(Pointer switcher, WString deviceId, int DeviceType, int DeviceRole);

    boolean get_device_collection(PointerByReference data, int type, int state);

    enum DeviceType {
        Playback, Recording,All;

        public int getValue() {
            return ordinal();
        }
    }

    enum DeviceRole {
        Console, Multimedia, Communications,All;

        public int getValue() {
            return ordinal();
        }
    }

    enum DeviceState{
        Active(1),Disabled(2),NotPresent(4),Unplugged(8),All(15);

        private int value;

        DeviceState(int value){
            this.value=value;
        }
        public int getValue() {
            return value;
        }
    }
}
