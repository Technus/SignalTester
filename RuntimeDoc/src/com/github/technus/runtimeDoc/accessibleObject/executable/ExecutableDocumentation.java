package com.github.technus.runtimeDoc.accessibleObject.executable;

import com.github.technus.runtimeDoc.accessibleObject.AccessibleObjectDocumentation;

import java.lang.annotation.ElementType;
import java.lang.reflect.Executable;

public abstract class ExecutableDocumentation<T extends Executable> extends AccessibleObjectDocumentation<T> {
    protected ExecutableDocumentation(T executable, ElementType type){
        super(executable,type);
    }
}
