package com.github.technus.signalTester.plugin;

import com.github.technus.signalTester.SignalTesterHeadless;

import java.util.HashMap;

public interface Plugin {
    void initialize(SignalTesterHeadless headless);
    void cleanup(SignalTesterHeadless headless);
    HashMap<Class<? extends PluginClass>, PluginClass> pluginClasses();
}
