package org.crain.dme4j.annotation;

import java.lang.annotation.*;

/**
 * Declares a static location in memory for an Object. This could be a pointer to the instance,
 * the instance itself, or a pointer to a pointer of the instance.
 * <br>
 * An alias is also allowed in order to handle multiple statics of the same type.
 */
@Repeatable(Statics.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Static {

    /**
     * Location in GameCube Memory where the Struct in.
     */
    long location();

    /**
     * Nickname wihtin a Query for this Static reference
     */
    String alias();

    /**
     * If this is a Pointer to a pointer, then this should be 1.
     * If this is a pointer to a pointer to a pointer, then this should be 2.
     */
    int pointerDepth() default 0;

}
