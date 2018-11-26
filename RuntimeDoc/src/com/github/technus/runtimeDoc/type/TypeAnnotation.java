package com.github.technus.runtimeDoc.type;

import com.github.technus.runtimeDoc.Documentation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documentation(ElementType.TYPE)
@Inherited
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
