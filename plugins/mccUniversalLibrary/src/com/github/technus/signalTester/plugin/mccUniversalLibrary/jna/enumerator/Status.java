package com.github.technus.signalTester.plugin.mccUniversalLibrary.jna.enumerator;

import java.util.HashMap;

public enum Status {
    IDLE(0,""),
    RUNNING(1,"");

    private static final HashMap<Integer, Status> map=new HashMap<>();
    public final String description;
    public final int value;

    Status(int value, String description) {
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

    public static Status get(int code){
        return map.get(code);
    }
}
