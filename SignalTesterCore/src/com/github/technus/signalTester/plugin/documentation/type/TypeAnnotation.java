package com.github.technus.signalTester.plugin.documentation.type;

import com.github.technus.signalTester.plugin.documentation.Documentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documentation(ElementType.TYPE)
public @interface TypeAnnotation {
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
