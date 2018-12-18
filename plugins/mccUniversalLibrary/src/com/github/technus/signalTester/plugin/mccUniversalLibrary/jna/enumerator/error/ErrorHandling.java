package com.github.technus.signalTester.plugin.mccUniversalLibrary.jna.enumerator.error;

import java.util.HashMap;

public enum ErrorHandling {
    DONTSTOP(0,"The program will always continue executing when an error occurs."),
    STOPFATAL(1,"The program will halt if a \"fatal\" error occurs."),
    STOPALL(2,"Will stop whenever any error occurs. If you are running in an Integrated Development Environment (IDE), when errors occur the environment may be shut down along with the program. If your IDE behaves this way, (QuickBasic and VisualBasic do), then set ErrHandling to DONTSTOP. Refer to the \"Error Codes\" topic for a complete list of error codes and their associated messages.");

    private static final HashMap<Integer, ErrorHandling> map=new HashMap<>();
    public final String description;
    public final int value;

    ErrorHandling(int value, String description) {
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

    public static ErrorHandling get(int code){
        return map.get(code);
    }
}
