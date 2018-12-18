package com.github.technus.signalTester.plugin.mccUniversalLibrary.jna.enumerator.error;

import java.util.HashMap;

public enum ErrorReporting {
    DONTPRINT(0,"Errors will not generate a message to the screen. In that case your program must always check the returned error code after each library call to determine if an error occurred."),
    PRINTWARNINGS(1,"Only warning errors will generate a message to the screen. Your program will have to check for fatal errors."),
    PRINTFATAL(2,"Only fatal errors will generate a message to the screen. Your program must check for warning errors."),
    PRINTALL(3,"All errors will generate a message to the screen.");

    private static final HashMap<Integer, ErrorReporting> map=new HashMap<>();
    public final String description;
    public final int value;

    ErrorReporting(int value, String description) {
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

    public static ErrorReporting get(int code){
        return map.get(code);
    }
}
