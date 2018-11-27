package com.github.technus.runtimeDoc.pack;

import com.github.technus.runtimeDoc.AnnotatedElementDocumentation;

import java.lang.annotation.ElementType;

public class PackageDocumentation extends AnnotatedElementDocumentation<Package> {
    public PackageDocumentation(Package pack){
        super(pack,ElementType.PACKAGE);
        //String name=element.getName();
        //int dot=name.lastIndexOf('.');
        //if(dot>=0){
        //    name=name.substring(0,dot);
        //    Package parent=Package.getPackage(name);
        //    if(parent!=null){
        //
        //    }
        //}
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
