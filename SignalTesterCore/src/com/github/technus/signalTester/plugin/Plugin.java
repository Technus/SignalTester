package com.github.technus.signalTester.plugin;

import com.github.technus.signalTester.SignalTesterHeadless;

public interface Plugin<T extends Plugin<T>> {
    void initialize(SignalTesterHeadless headless);
    void cleanup(SignalTesterHeadless headless);
    PluginDocumentation<T> pluginDocumentation();
    default String getName(){
        return getClass().getSimpleName();
    }
    default String getVerion(){
        return "1.0-SNAPSHOT";
    }
}
