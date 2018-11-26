package com.github.technus.signalTester.plugin.documentation.member.executable;

import com.github.technus.signalTester.plugin.documentation.member.MemberDocumentation;

import java.lang.annotation.ElementType;
import java.lang.reflect.Executable;

public abstract class ExecutableDocumentation<T extends Executable> extends MemberDocumentation<T> {
    protected ExecutableDocumentation(T executable, ElementType type){
        super(executable,type);
    }
    protected ExecutableDocumentation(Class clazz,T executable, ElementType type){
        super(executable,type);
    }
}
