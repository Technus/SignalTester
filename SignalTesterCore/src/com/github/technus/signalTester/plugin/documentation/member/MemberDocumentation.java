package com.github.technus.signalTester.plugin.documentation.member;

import com.github.technus.signalTester.plugin.documentation.AnnotatedElementDocumentation;

import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;

public abstract class MemberDocumentation<T extends Member & AnnotatedElement> extends AnnotatedElementDocumentation<T> {
    public MemberDocumentation(T member, ElementType type){
        super(member,type);
    }
}
