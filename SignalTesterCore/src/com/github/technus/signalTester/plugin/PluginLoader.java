package com.github.technus.signalTester.plugin;

import com.github.technus.signalTester.SignalTesterHeadless;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
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

    public void loadWithoutPath(){
        ServiceLoader<Plugin> loader=ServiceLoader.load(Plugin.class, Plugin.class.getClassLoader());
        loader.iterator().forEachRemaining(plugin -> {
            if(!plugins.containsKey(plugin.getClass())) {
                plugin.initialize(headless);
                plugins.put(plugin.getClass(), plugin);
            }
        });
    }

    public void loadWithPath(File parentPluginFile) throws MalformedURLException {
        ArrayList<URL> urlArrayList=new ArrayList<>();
        if(parentPluginFile.isDirectory()) {
            addFolderContents(urlArrayList, parentPluginFile);
        }else {
            urlArrayList.add(parentPluginFile.toURI().toURL());
        }
        URLClassLoader urlClassLoader = URLClassLoader.newInstance(urlArrayList.toArray(new URL[0]), Plugin.class.getClassLoader());
        ServiceLoader<Plugin> pluginServiceLoader = ServiceLoader.load(Plugin.class, urlClassLoader);
        pluginServiceLoader.iterator().forEachRemaining(plugin -> plugins.putIfAbsent(plugin.getClass(), plugin));
    }

    private void addFolderContents(ArrayList<URL> urlArrayList,File parentDir) throws MalformedURLException{
        if(parentDir.canRead() && !Files.isSymbolicLink(parentDir.toPath())){
            File[] files = parentDir.listFiles((jarFile, name) -> name.endsWith(".jar") && jarFile.isFile() && jarFile.canRead());
            if (files != null) {
                for (File file : files) {
                    urlArrayList.add(file.toURI().toURL());
                }
            }
            File[] dirs=parentDir.listFiles(File::isDirectory);
            if(dirs!=null){
                for(File dir:dirs){
                    addFolderContents(urlArrayList,dir);
                }
            }
        }
    }

    public ObservableMap<Class<? extends Plugin>,Plugin> getPlugins() {
        return plugins;
    }
}
