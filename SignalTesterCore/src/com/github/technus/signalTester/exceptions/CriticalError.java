package com.github.technus.signalTester.exceptions;

public class CriticalError extends Error {
    public CriticalError(){
        super();
    }
    public CriticalError(String msg){
        super(msg);
    }
    public CriticalError(String msg,Throwable cause){
        super(msg,cause);
    }
    public CriticalError(Throwable cause){
        super(cause);
    }
}
