package com.github.technus.signalTester.utility;

import java.util.Arrays;

public class AudioDevice implements Comparable<AudioDevice>{
    public final String friendlyName, deviceName,id, interfaceName;
    public final AudioSwitcherLibrary.DeviceState state;
    public final AudioSwitcherLibrary.DeviceType type;

    AudioDevice(String[] fields, AudioSwitcherLibrary.DeviceType type, AudioSwitcherLibrary.DeviceState state) throws Exception{
        if(fields.length==4){
            id=fields[0];
            deviceName =fields[1];
            friendlyName =fields[2];
            interfaceName =fields[3];
            this.type=type;
            this.state=state;
        }else{
            throw new Exception("Invalid string for MMDevice: "+Arrays.toString(fields)+" "+type+" "+state);
        }
    }

    @Override
    public int compareTo(AudioDevice o) {
        return id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object obj) {
        return compareTo((AudioDevice)obj)==0;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id+"|"+ deviceName +"|"+ friendlyName +"|"+ interfaceName +"|"+type+"|"+state;
    }
}
