package com.github.technus.signalTester.plugin.jython;

import com.github.technus.signalTester.SignalTesterHeadless;
import com.github.technus.signalTester.plugin.Plugin;
import com.github.technus.signalTester.plugin.PluginDocumentation;

import java.util.HashMap;

public class JythonPlugin implements Plugin {
    @Override
    public void initialize(SignalTesterHeadless headless) {

    }

    @Override
    public void cleanup(SignalTesterHeadless headless) {

    }

    @Override
    public HashMap<Class<? extends PluginDocumentation>, PluginDocumentation> pluginClasses() {
        return null;
    }
}
