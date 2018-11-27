package com.github.technus.signalTester.plugin;

import com.github.technus.signalTester.SignalTesterHeadless;

public interface Plugin<T> {
    void initialize(SignalTesterHeadless headless);
    void cleanup(SignalTesterHeadless headless);
    PluginDocumentation<Plugin<T>> pluginDocumentation();
}
