package com.github.technus.signalTester.plugin.mccUniversalLibrary.jna.enumerator;

import java.util.ArrayList;

public enum CalibrationCoefficientType {
    COARSE_GAIN(0x01,""),
    COARSE_OFFSET(0x02,""),
    FINE_GAIN(0x04,""),
    FINE_OFFSET(0x08,"");
    //@Deprecated
    //GAIN(COARSE_GAIN.value,""),
    //@Deprecated
    //OFFSET(COARSE_OFFSET.value,"");

    public final String description;
    public final int value;

    CalibrationCoefficientType(int value, String description) {
        this.value = value;
        this.description = description.equals("")?this.name():description;
    }

    public static ArrayList<CalibrationCoefficientType> get(int i){
        ArrayList<CalibrationCoefficientType> types=new ArrayList<>();
        for(CalibrationCoefficientType type: CalibrationCoefficientType.values()){
            if((i&type.value)==type.value){
                types.add(type);
            }
        }
        return types;
    }
}
