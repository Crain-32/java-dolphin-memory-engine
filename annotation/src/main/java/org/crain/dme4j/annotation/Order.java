package org.crain.dme4j.annotation;

import java.lang.annotation.*;

/**
 * As field order isn't guaranteed by the JVM, this allows a way to ensure an order within your struct.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.METHOD, ElementType.FIELD
})
public @interface Order {
    /**
     * Order is sorted from lowest to highest. <br>
     * Given the following Address - Value pairs
     * <ul>
     * <li>0x00 - 0x42</li>
     * <li>0x01 - 0x83</li>
     * </ul>
     * The address key aligns with the expected Order. <br>
     * Negative values can be used, but really?
     */
    int value() default -1;
}
