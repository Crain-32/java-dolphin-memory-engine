package org.crain.dme4j.annotation;

import java.lang.annotation.*;

/**
 * This exposes behavior similar to JPA's FetchType, being that a field marked with this is expected to either
 * be included or excluded from any query based on the FetchType.
 *
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Pointer {

    /**
     * The default behavior for Pointers is to be LAZY, eager leads to the class N+1 issue.
     */
    FetchType type() default FetchType.LAZY;

    /**
     * Used only when annotated on a String Field/Method. Declared the maximum length of the buffer usable.
     */
    int stringLength() default 8;
    /**
     * As noted above, please reference JPA's FetchType for a better understanding of the problem this is trying to
     * solve.
     * TLDR -> LAZY doesn't include the field in a query, EAGER does include the field in the query.
     */
    enum FetchType {
        LAZY,
        EAGER
    }
}
