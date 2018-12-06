package com.github.technus.runtimeDoc.annotatedElement.type.use;

import com.github.technus.runtimeDoc.annotatedElement.Documentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_USE)
@Documentation(ElementType.TYPE_USE)
public @interface TypeUseAnnotation {
    /**
     * Just Name of the thing
     * @return
     */
    String name() default "";

    /**
     * Description of whatever is in the same line in code (related to this)
     * @return
     */
    String declaration() default "";

    /**
     * What is the thing used for, and how it should be used
     * @return
     */
    String descriptionTag() default "";
}
