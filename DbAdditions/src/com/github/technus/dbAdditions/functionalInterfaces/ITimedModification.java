package com.github.technus.dbAdditions.functionalInterfaces;

import java.time.Instant;

public interface ITimedModification {
    Instant getTimestamp();
    static <T extends ITimedModification> T getNewest(T... timedModifications){
        if(timedModifications!=null && timedModifications.length>0) {
            T newestObject = timedModifications[0];
            for (int i = 1; i < timedModifications.length; i++) {
                T currentlyTestedObject = timedModifications[i];
                if(currentlyTestedObject.getTimestamp().isAfter(newestObject.getTimestamp())){
                    newestObject=currentlyTestedObject;
                }
            }
            return newestObject;
        }else {
            return null;
        }
    }
}
