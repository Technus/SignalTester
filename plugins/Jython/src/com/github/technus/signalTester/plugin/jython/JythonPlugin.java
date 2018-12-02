package com.github.technus.signalTester.plugin.jython;

import com.github.technus.runtimeDoc.type.ClassDocumentation;
import com.github.technus.signalTester.SignalTesterHeadless;
import com.github.technus.signalTester.plugin.Plugin;
import com.github.technus.signalTester.plugin.PluginDocumentation;

public class JythonPlugin implements Plugin<JythonPlugin> {
    @Override
    public void initialize(SignalTesterHeadless headless) {

    }

    @Override
    public void cleanup(SignalTesterHeadless headless) {

    }

    @Override
    public PluginDocumentation<JythonPlugin> pluginDocumentation() {
        return new PluginDocumentation<JythonPlugin>(this) {
            @Override
            protected void loadAllDocumentation() {
                classes.put(JythonPlugin.class,new ClassDocumentation(JythonPlugin.class));
            }
        };
    }
}
