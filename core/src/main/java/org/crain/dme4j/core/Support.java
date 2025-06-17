package org.crain.dme4j.core;

import java.lang.annotation.*;

/**
 * This annotation is intended to be put onto Implementations of {@link GamecubeMemoryEngine} to allow for
 * usage with the {@link java.util.ServiceLoader ServiceLoader} abstraction. Colliding values are allowed,
 * it is assumed the underlying class type would be checked in that case.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Support {

    /**
     * An array of supported connections for this implementation. For example {"WINDOWS","LINUX","MACOS"}
     */
    String[] value() default "UNKNOWN";

    /**
     * If the provided implementation can support read actions against the Gamecube Memory Space
     * Expected to mirror {@link GamecubeMemoryEngine#canRead()}
     * Reading is expected by default as it is an easier operation to support than writing.
     */
    boolean canRead() default true;

    /**
     * If the provided implementation can support write actions against the Gamecube Memory Space
     * Expected to mirror {@link GamecubeMemoryEngine#canWrite()}
     */
    boolean canWrite() default false;

}
