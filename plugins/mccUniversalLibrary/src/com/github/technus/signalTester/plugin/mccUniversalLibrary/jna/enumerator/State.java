package com.github.technus.signalTester.plugin.mccUniversalLibrary.jna.enumerator;

import java.util.HashMap;

public enum State {
    ENABLED(1,""),
    DISABLED(0,"");

    private static final HashMap<Integer, State> map=new HashMap<>();
    public final String description;
    public final int value;

    State(int value, String description) {
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

    public static State get(int code){
        return map.get(code);
    }
}
