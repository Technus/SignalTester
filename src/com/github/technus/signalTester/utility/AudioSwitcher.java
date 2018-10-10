package com.github.technus.signalTester.utility;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.PointerByReference;

import java.util.HashSet;

public class AudioSwitcher {
    private Pointer instance;

    public AudioSwitcher(){
        instance=AudioSwitcherLibrary.INSTANCE.get_instance();
    }

    public boolean setDevice(AudioDevice audioDevice, AudioSwitcherLibrary.DeviceRole role){
        return AudioSwitcherLibrary.INSTANCE.switch_to(new WString(audioDevice.id),role.getValue());
    }

    public boolean isDefault(AudioDevice audioDevice, AudioSwitcherLibrary.DeviceType type, AudioSwitcherLibrary.DeviceRole role){
        return AudioSwitcherLibrary.INSTANCE.is_default(instance,new WString(audioDevice.id),type.getValue(),role.getValue());
    }

    private void getDevicesList(HashSet<AudioDevice> devices, AudioSwitcherLibrary.DeviceType type, AudioSwitcherLibrary.DeviceState state){
        PointerByReference pointer=new PointerByReference();
        AudioSwitcherLibrary.INSTANCE.get_device_collection(pointer,type.getValue(),state.getValue());

        String string=pointer.getValue().getWideString(0);
        if(string.length()==0){
            return;
        }
        for(String s:string.split("\\n")){
            try {
                devices.add(new AudioDevice(s.split("\\|"),type,state));
            }catch (Exception e){
                Utility.showThrowableMain(null,e,"Error getting audio devices");
            }
        }
    }

    public AudioDevice[] getDevices(AudioSwitcherLibrary.DeviceType type,AudioSwitcherLibrary.DeviceState state){
        HashSet<AudioDevice> set=new HashSet<>();
        if(type==AudioSwitcherLibrary.DeviceType.All && state==AudioSwitcherLibrary.DeviceState.All){
            for(AudioSwitcherLibrary.DeviceType type1:AudioSwitcherLibrary.DeviceType.values()){
                if(type1==AudioSwitcherLibrary.DeviceType.All) continue;
                for(AudioSwitcherLibrary.DeviceState state1:AudioSwitcherLibrary.DeviceState.values()){
                    if(state1==AudioSwitcherLibrary.DeviceState.All) continue;
                    getDevicesList(set,type1,state1);
                }
            }
        }else if(type==AudioSwitcherLibrary.DeviceType.All){
            for(AudioSwitcherLibrary.DeviceType type1:AudioSwitcherLibrary.DeviceType.values()){
                if(type1==AudioSwitcherLibrary.DeviceType.All) continue;
                getDevicesList(set,type1,state);
            }
        }else if(state==AudioSwitcherLibrary.DeviceState.All){
            for(AudioSwitcherLibrary.DeviceState state1:AudioSwitcherLibrary.DeviceState.values()){
                if(state1==AudioSwitcherLibrary.DeviceState.All) continue;
                getDevicesList(set,type,state1);
            }
        }else{
            getDevicesList(set,type,state);
        }
        return set.toArray(new AudioDevice[0]);
    }

    public AudioDevice[] getPresentDevices(AudioSwitcherLibrary.DeviceType type,AudioSwitcherLibrary.DeviceState state){
        HashSet<AudioDevice> set=new HashSet<>();
        if(type==AudioSwitcherLibrary.DeviceType.All && state==AudioSwitcherLibrary.DeviceState.All){
            for(AudioSwitcherLibrary.DeviceType type1:AudioSwitcherLibrary.DeviceType.values()){
                if(type1==AudioSwitcherLibrary.DeviceType.All) continue;
                for(AudioSwitcherLibrary.DeviceState state1:AudioSwitcherLibrary.DeviceState.values()){
                    if(state1==AudioSwitcherLibrary.DeviceState.All || state1==AudioSwitcherLibrary.DeviceState.NotPresent) continue;
                    getDevicesList(set,type1,state1);
                }
            }
        }else if(type==AudioSwitcherLibrary.DeviceType.All){
            for(AudioSwitcherLibrary.DeviceType type1:AudioSwitcherLibrary.DeviceType.values()){
                if(type1==AudioSwitcherLibrary.DeviceType.All) continue;
                getDevicesList(set,type1,state);
            }
        }else if(state==AudioSwitcherLibrary.DeviceState.All){
            for(AudioSwitcherLibrary.DeviceState state1:AudioSwitcherLibrary.DeviceState.values()){
                if(state1==AudioSwitcherLibrary.DeviceState.All || state1==AudioSwitcherLibrary.DeviceState.NotPresent) continue;
                getDevicesList(set,type,state1);
            }
        }else if(state!=AudioSwitcherLibrary.DeviceState.NotPresent){
            getDevicesList(set,type,state);
        }
        return set.toArray(new AudioDevice[0]);
    }
}
