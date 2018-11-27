package com.github.technus.signalTester.plugin;

import com.github.technus.signalTester.SignalTesterHeadless;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ServiceLoader;

public class PluginLoader{
    private final SignalTesterHeadless headless;
    private final ObservableMap<Class<? extends Plugin>,Plugin> plugins= FXCollections.observableHashMap();

    public PluginLoader(SignalTesterHeadless headless){
        this.headless=headless;
        plugins.addListener((MapChangeListener<Class<? extends Plugin>, Plugin>) change -> {
            if(change.wasRemoved()) {
                change.getValueRemoved().cleanup(this.headless);
            }
            if(change.wasAdded()){
                change.getValueAdded().initialize(this.headless);
            }
        });
    }

    public PluginLoader withoutPath(){
        ServiceLoader<Plugin> loader=ServiceLoader.load(Plugin.class);
        loader.iterator().forEachRemaining(plugin -> {
            if(!plugins.containsKey(plugin.getClass())) {
                plugin.initialize(headless);
                plugins.put(plugin.getClass(), plugin);
            }
        });
        return this;
    }

    public PluginLoader withPath(File directory) throws MalformedURLException {
        //todo for each directory?
        ServiceLoader<Plugin> loader=ServiceLoader.load(Plugin.class,
                URLClassLoader.newInstance(new URL[]{directory.toURI().toURL()},ClassLoader.getSystemClassLoader()));
        loader.iterator().forEachRemaining(plugin -> plugins.putIfAbsent(plugin.getClass(),plugin));
        return this;
    }

    public ObservableMap<Class<? extends Plugin>,Plugin> getPlugins() {
        return plugins;
    }
}
