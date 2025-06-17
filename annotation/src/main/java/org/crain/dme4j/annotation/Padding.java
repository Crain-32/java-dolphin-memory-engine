package org.crain.dme4j.annotation;

import java.lang.annotation.*;

/**
 * As compilers can generate space between different fields in a struct, this
 * annotation provides a way to specify padding without using a dummy field in a
 * constructor.
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Padding {

    /**
     * Length of the Padding in Bytes, if -1 or 0 then no padding is applied.
     */
    int value() default -1;

    /**
     * Where the padding should be in relationship to the field it is associated to.
     * For example given the following,
     * <pre>
     * {@code
     * @String(
     *   name = "Example",
     *   fields = {
     *     @Field(
     *       name = "field1",
     *       order = @Order(1),
     *       padding = @Padding(value = 4, paddingType = PaddingType.After)
     *     ),
     *     @Field(
     *       name = "field2",
     *       order = @Order(2),
     *     )
     *   }
     * )
     * }
     * </pre>
     * The padding would be placed between {@code field1} and {@code field2}.
     */
    PaddingType paddingType() default PaddingType.BEFORE;

    /**
     * Where the padding should be in relationship to the field that is padded.
     */
    enum PaddingType {
        BEFORE,
        AFTER
    }


}
