package com.github.technus.signalTester.plugin.mccUniversalLibrary.jna.enumerator;

import java.util.HashMap;

public enum Function {
    NOFUNCTION(0,"No Function"),
    AIFUNCTION(1,"Analog Input Function"),
    AOFUNCTION(2,"Analog Output Function"),
    DIFUNCTION(3,"Digital Input Function"),
    DOFUNCTION(4,"Digital Output Function"),
    CTRFUNCTION(5,"Counter Function"),
    DAQIFUNCTION(6,"Daq Input Function"),
    DAQOFUNCTION(7,"Daq Output Function");

    private static final HashMap<Integer, Function> map=new HashMap<>();
    public final String description;
    public final int value;

    Function(int value, String description) {
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

    public static Function get(int code){
        return map.get(code);
    }
}
