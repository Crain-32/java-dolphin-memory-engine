package org.crain.dme4j.annotation;

import java.lang.annotation.*;

/**
 * Annotation to help map a POJO field into the Gamecube Memory Layout.
 *
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Field {

    /**
     * This name is used if you use a projection, or get the back object as a raw Struct instance.
     */
    String value() default "";

    /**
     * Order takes precedent over any other ordering method. If the value is negative than return order in the
     * reflection analysis is used.
     */
    Order order() default @Order(-1);

    /**
     * Allows for padding the struct without inserting dummy fields.
     */
    Padding padding() default @Padding();

    /**
     * If this Field references a Pointer, then this allows for overriding the default behavior of that Pointer.
     *
     */
    Pointer pointer() default @Pointer();

    /**
     * If the getter for this field does not align with the Java Bean convention, then this field allows you to override
     * the expectation.
     */
    String get() default "";

    /**
     * Mirror of {@link Field#get()}, but for setters.
     */
    String set() default "";

    /**
     * Only used if the type is not a record or value class (once those are in Java)
     */
    boolean inline() default false;

}
