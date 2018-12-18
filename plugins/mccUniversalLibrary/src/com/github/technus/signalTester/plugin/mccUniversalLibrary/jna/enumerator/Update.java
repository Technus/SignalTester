package com.github.technus.signalTester.plugin.mccUniversalLibrary.jna.enumerator;

import java.util.HashMap;

public enum Update {
    UPDATEIMMEDIATE(0,""),
    UPDATEONCOMMAND(1,"");

    private static final HashMap<Integer, Update> map=new HashMap<>();
    public final String description;
    public final int value;

    Update(int value, String description) {
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

    public static Update get(int code){
        return map.get(code);
    }
}
