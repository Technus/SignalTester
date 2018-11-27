package com.github.technus.signalTester.plugin;

import com.github.technus.runtimeDoc.accessibleObject.executable.constructor.ConstructorDocumentation;
import com.github.technus.runtimeDoc.accessibleObject.field.FieldDocumentation;
import com.github.technus.runtimeDoc.accessibleObject.executable.method.MethodDocumentation;
import com.github.technus.runtimeDoc.pack.PackageDocumentation;
import com.github.technus.runtimeDoc.type.ClassDocumentation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class PluginDocumentation<T extends Plugin> {
    public final Plugin<T> plugin;
    public final HashMap<Class,ClassDocumentation> classes=new HashMap<>();
    public final HashMap<Package,PackageDocumentation> packages=new HashMap<>();
    public final HashMap<Field, FieldDocumentation> fields=new HashMap<>();
    public final HashMap<Method, MethodDocumentation> methods=new HashMap<>();
    public final HashMap<Constructor<T>,ConstructorDocumentation<T>> constructors=new HashMap<>();

    public PluginDocumentation(Plugin<T> plugin){
        this.plugin=plugin;
    }
}
