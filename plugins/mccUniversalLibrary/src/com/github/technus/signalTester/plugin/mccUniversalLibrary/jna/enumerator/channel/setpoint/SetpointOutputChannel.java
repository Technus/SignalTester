package com.github.technus.signalTester.plugin.mccUniversalLibrary.jna.enumerator.channel.setpoint;

import java.util.HashMap;

public enum SetpointOutputChannel {
    SO_NONE(0,"No Output"),
    SO_DIGITALPORT(1,"Output to digital Port"),
    SO_FIRSTPORTC(1,"Output to first PortC"),
    SO_DAC0(2,"Output to DAC0"),
    SO_DAC1(3,"Output to DAC1"),
    SO_DAC2(4,"Output to DAC2"),
    SO_DAC3(5,"Output to DAC3"),
    SO_TMR0(6,"Output to TMR0"),
    SO_TMR1(7,"Output to TMR1");

    private static final HashMap<Integer, SetpointOutputChannel> map=new HashMap<>();
    public final String description;
    public final int value;

    SetpointOutputChannel(int value, String description) {
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

    public static SetpointOutputChannel get(int code){
        return map.get(code);
    }
}
