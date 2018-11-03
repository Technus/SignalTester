package com.github.technus.signalTester.fx.main;

import com.github.technus.signalTester.SignalTesterHeadless;
import com.github.technus.signalTester.fx.SignalTester;

public class MainModel {
    public final SignalTesterHeadless headless;

    public MainModel(){
        headless=new SignalTesterHeadless(SignalTester.parameters);
    }

    public void initialize() throws Exception{
        headless.initialize();
    }
}
