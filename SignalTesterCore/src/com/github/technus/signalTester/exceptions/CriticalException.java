package com.github.technus.signalTester.exceptions;

public class CriticalException extends Exception {
    public CriticalException(){
        super();
    }
    public CriticalException(String msg){
        super(msg);
    }
    public CriticalException(String msg,Throwable cause){
        super(msg,cause);
    }
    public CriticalException(Throwable cause){
        super(cause);
    }
}
