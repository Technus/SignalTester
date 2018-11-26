package com.github.technus.runtimeDoc.pack;

import com.github.technus.runtimeDoc.AnnotatedElementDocumentation;

import java.lang.annotation.ElementType;

public class PackageDocumentation extends AnnotatedElementDocumentation<Package> {
    public PackageDocumentation(Package pack){
        super(pack,ElementType.PACKAGE);
    }

    @Override
    protected String setName() {
        String name=element.getName();
        int dot=name.lastIndexOf('.');
        if(dot>0) {
            return name.substring(dot + 1);
        }
        return name;
    }

    @Override
    protected String setDeclaration() {
        return element.getName();
    }

    @Override
    protected String setDescriptionTag() {
        return element.getName();
    }
}
