package com.github.technus.signalTester.plugin.mccUniversalLibrary.jna.enumerator;

import java.util.HashMap;

public enum Constant {
    /* Maximum length of error string */
    ERRSTRLEN(256,""),

    /* Maximum length of board name */
    BOARDNAMELEN(64,""),

    /* Arguments that are used in a particular function call should be set
       to NOTUSED */
    NOTUSED(-1,"");

    private static final HashMap<Integer, Constant> map=new HashMap<>();
    public final String description;
    public final int value;

    Constant(int value, String description) {
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

    public static Constant get(int code){
        return map.get(code);
    }
}
