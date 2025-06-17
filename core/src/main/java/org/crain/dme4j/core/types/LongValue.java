package org.crain.dme4j.core.types;

/**
 * {@link Value}
 */
public final class LongValue extends Value<Long> {

    /**
     * Default value is 0L
     */
    public LongValue() {
        super(0L, 8);
    }

    public LongValue(Long value) {
        super(value, 8);
    }
}
