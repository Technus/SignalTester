package com.github.technus.signalTester.plugin.mccUniversalLibrary.jna.enumerator.trigger;

import java.util.HashMap;

public enum TriggerSensitivity {
    RISING_EDGE(0,""),
    FALLING_EDGE(1,""),
    ABOVE_LEVEL(2,""),
    BELOW_LEVEL(3,""),
    EQ_LEVEL(4,""),
    NE_LEVEL(5,""),
    HIGH_LEVEL(6,""),
    LOW_LEVEL(7,"");

    private static final HashMap<Integer, TriggerSensitivity> map=new HashMap<>();
    public final String description;
    public final int value;

    TriggerSensitivity(int value, String description) {
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

    public static TriggerSensitivity get(int code){
        return map.get(code);
    }
}
