package com.github.technus.signalTester.plugin.mccUniversalLibrary.jna.enumerator.trigger;

import java.util.HashMap;

public enum TriggerEvent {
    START_EVENT(0,""),
    STOP_EVENT(1,"");

    private static final HashMap<Integer, TriggerEvent> map=new HashMap<>();
    public final String description;
    public final int value;

    TriggerEvent(int value, String description) {
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

    public static TriggerEvent get(int code){
        return map.get(code);
    }
}
