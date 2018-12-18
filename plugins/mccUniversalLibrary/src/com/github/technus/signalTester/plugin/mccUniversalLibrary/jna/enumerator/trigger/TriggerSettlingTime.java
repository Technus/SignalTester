package com.github.technus.signalTester.plugin.mccUniversalLibrary.jna.enumerator.trigger;

import java.util.HashMap;

public enum TriggerSettlingTime {
    SETTLE_DEFAULT(0,""),
    SETTLE_1us(1,""),
    SETTLE_5us(2,""),
    SETTLE_10us(3,""),
    SETTLE_1ms(4,"");

    private static final HashMap<Integer, TriggerSettlingTime> map=new HashMap<>();
    public final String description;
    public final int value;

    TriggerSettlingTime(int value, String description) {
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

    public static TriggerSettlingTime get(int code){
        return map.get(code);
    }
}
