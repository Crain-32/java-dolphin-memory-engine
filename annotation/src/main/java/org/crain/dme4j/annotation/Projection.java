package org.crain.dme4j.annotation;

import java.lang.annotation.*;

/**
 * This annotation can only be placed on interfaces. It allows for queries to define a partial
 * structure to return from the query.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Projection {

    /**
     * Underlying Class this Projection maps to.
     */
    Class<?> structClass();

    /**
     * Alternative to {@link Projection#structClass()}, which accepts FQN of the class, or an Alias
     */
    String struct();
}
