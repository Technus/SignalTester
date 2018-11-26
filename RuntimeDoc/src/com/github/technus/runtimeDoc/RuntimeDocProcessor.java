package com.github.technus.runtimeDoc;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;
//import javax.lang.model.element;
//import javax.lang.model.type;

@SupportedAnnotationTypes({
        "com.github.technus.runtimeDoc.type.TypeAnnotation",
        "com.github.technus.runtimeDoc.type.parameter.TypeParameterAnnotation",
        "com.github.technus.runtimeDoc.type.use.TypeUseAnnotation",
        "com.github.technus.runtimeDoc.parameter.ParameterAnnotation",
        "com.github.technus.runtimeDoc.pack.PackageAnnotation",
        "com.github.technus.runtimeDoc.accessibleObject.field.FieldAnnotation",
        "com.github.technus.runtimeDoc.accessibleObject.executable.method.MethodAnnotation",
        "com.github.technus.runtimeDoc.accessibleObject.executable.constructor.ConstructorAnnotation",})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class RuntimeDocProcessor extends AbstractProcessor {
    public static Set<CharSequence> documented=new HashSet<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        TypeElement element=processingEnv.getElementUtils().getTypeElement("com.github.technus.runtimeDoc.type.TypeAnnotation");
        DeclaredType type=processingEnv.getTypeUtils().getDeclaredType(element);
        Set<? extends Element> annotated=roundEnv.getElementsAnnotatedWith(element);
        for(TypeElement typeElement:ElementFilter.typesIn(annotated)){
            documented.add(typeElement.getSimpleName());
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,typeElement.getSimpleName(),typeElement);
        }
        return false;
    }
}
