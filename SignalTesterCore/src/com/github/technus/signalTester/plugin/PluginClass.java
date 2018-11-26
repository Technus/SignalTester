package com.github.technus.signalTester.plugin;

import com.github.technus.signalTester.plugin.documentation.member.field.FieldDocumentation;
import com.github.technus.signalTester.plugin.documentation.member.executable.method.MethodDocumentation;
import com.github.technus.signalTester.plugin.documentation.type.TypeDocumentation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class PluginClass<T> {
    public final Class<T> clazz;
    public final TypeDocumentation<T> type;
    public final HashMap<Field, FieldDocumentation> fields=new HashMap<>();
    public final HashMap<Method, MethodDocumentation> methods=new HashMap<>();
    public final HashMap<Constructor<T>,MethodDocumentation> constructors=new HashMap<>();

    public PluginClass(Class<T> clazz){
        this.clazz=clazz;
        type=new TypeDocumentation<>(clazz);
    }
}
