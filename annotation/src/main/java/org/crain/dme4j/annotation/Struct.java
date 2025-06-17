package org.crain.dme4j.annotation;

import java.lang.annotation.*;

/**
 * Defines the mapping from POJO -> Struct. </br>
 * Name is a unique identifier across all types. </br>
 * The fields property is an optional alternative declaration. It cannot be combined
 * with the free form used of field, except for in the case of inheritance.
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR})
public @interface Struct {

    /**
     * Unique name for this Struct, can be used in place of the Fully Qualified Class Name.
     */
    String name() default "";

    /**
     * {@link Field} alternative declaration.
     */
    Field[] fields() default {};

}
