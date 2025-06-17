package org.crain.dme4j.core.types;

/**
 * {@link Value}
 */
public final class ShortValue extends Value<Short> {

    /**
     * Default value is 0
     */
    public ShortValue() {
        super((short) 0, 2);
    }

    public ShortValue(Short value) {
        super(value, 2);
    }
}
