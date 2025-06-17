package org.crain.dme4j.annotation;

import java.lang.annotation.*;

/**
 * Container for {@link Static}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Statics {

    /**
     * Array of Static references, {@code Static.alias} must be unique across all of them.
     */
    Static[] value();
}
