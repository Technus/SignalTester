package com.github.technus.signalTester.plugin.mccUniversalLibrary.jna.enumerator.channel;

import java.util.HashMap;

public enum ChannelFlag {
    /* channel type flags*/
    SETPOINT_ENABLE(0x100,"Enable setpoint detection");

    private static final HashMap<Integer, ChannelFlag> map=new HashMap<>();
    public final String description;
    public final int value;

    ChannelFlag(int value, String description) {
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

    public static ChannelFlag get(int code){
        return map.get(code);
    }
}
