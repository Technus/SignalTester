package com.github.technus.signalTester.plugin.mccUniversalLibrary.jna.enumerator.channel;

import java.util.HashMap;

public enum ChannelType {
    ANALOG(0,"Analog channel"),
    DIGITAL8(1,"8-bit digital port"),
    DIGITAL16(2,"16-bit digital port"),
    CTR16(3,"16-bit counter"),
    CTR32LOW(4,"Lower 16-bits of 32-bit counter"),
    CTR32HIGH(5,"Upper 16-bits of 32-bit counter"),
    CJC(6,"CJC channel"),
    TC(7,"Thermocouple channel"),
    ANALOG_SE(8,"Analog channel, single-ended mode"),
    ANALOG_DIFF(9,"Analog channel, Differential mode"),
    SETPOINTSTATUS(10,"Setpoint status channel"),
    CTRBANK0(11,"Bank 0 of counter"),
    CTRBANK1(12,"Bank 1 of counter"),
    CTRBANK2(13,"Bank 2 of counter"),
    CTRBANK3(14,"Bank 3 of counter"),
    PADZERO(15,"Dummy channel. Fills the corresponding data elements with zero"),
    DIGITAL(16,""),
    CTR(17,"");

    private static final HashMap<Integer, ChannelType> map=new HashMap<>();
    public final String description;
    public final int value;

    ChannelType(int value, String description) {
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

    public static ChannelType get(int code){
        return map.get(code);
    }
}
