package com.github.technus.signalTester.plugin.mccUniversalLibrary.jna.enumerator.channel.setpoint;

import java.util.HashMap;

public enum SetpointFlag {
    SF_EQUAL_LIMITA(0x00,"Channel = LimitA value "),
    SF_LESSTHAN_LIMITA(0x01,"Channel < LimitA value"),
    SF_INSIDE_LIMITS(0x02,"Channel Inside LimitA and LimitB (LimitA < Channel < LimitB)"),
    SF_GREATERTHAN_LIMITB(0x03,"Channel > LimitB"),
    SF_OUTSIDE_LIMITS(0x04,"Channel Outside LimitA and LimitB (LimitA < Channel or Channel > LimitB)"),
    SF_HYSTERESIS(0x05,"Use As Hysteresis"),
    SF_UPDATEON_TRUEONLY(0x00,"Latch output condition (output = output1 for duration of acquisition)"),
    SF_UPDATEON_TRUEANDFALSE(0x08,"Do not latch output condition (output = output1 when criteria met else output = output2)");

    private static final HashMap<Integer, SetpointFlag> map=new HashMap<>();
    public final String description;
    public final int value;

    SetpointFlag(int value, String description) {
        this.value = value;
        this.description = description.equals("")?this.name():description;
        register();
    }

    private void register(){
        if(map.containsKey(value)){
            throw new EnumConstantNotPresentException(this.getClass(),this.name());
        }
        map.put(value,this);
    }

    public static SetpointFlag get(int code){
        return map.get(code);
    }
}
