package com.github.technus.runtimeDoc.annotatedElement.pack;

import com.github.technus.runtimeDoc.annotatedElement.AnnotatedElementDocumentation;

import java.lang.annotation.ElementType;

public class PackageDocumentation extends AnnotatedElementDocumentation<Package> {
    public PackageDocumentation(Package pack){
        super(pack,ElementType.PACKAGE);
    }

    @Override
    protected String fillName() {
        String name=element.getName();
        int dot=name.lastIndexOf('.');
        if(dot>=0) {
            return name.substring(dot + 1);
        }
        return name;
    }

    @Override
    protected String fillDeclaration() {
        return element.getName();
    }

    @Override
    protected String fillDescriptionTag() {
        return element.getName();
    }
}
