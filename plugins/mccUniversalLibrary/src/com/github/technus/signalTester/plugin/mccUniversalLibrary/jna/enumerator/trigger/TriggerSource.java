package com.github.technus.signalTester.plugin.mccUniversalLibrary.jna.enumerator.trigger;

import java.util.HashMap;

public enum TriggerSource {
    TRIG_IMMEDIATE(0,""),
    TRIG_EXTTTL(1,""),
    TRIG_ANALOG_HW(2,""),
    TRIG_ANALOG_SW(3,""),
    TRIG_DIGPATTERN(4,""),
    TRIG_COUNTER(5,""),
    TRIG_SCANCOUNT(6,"");

    private static final HashMap<Integer, TriggerSource> map=new HashMap<>();
    public final String description;
    public final int value;

    TriggerSource(int value, String description) {
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

    public static TriggerSource get(int code){
        return map.get(code);
    }
}
