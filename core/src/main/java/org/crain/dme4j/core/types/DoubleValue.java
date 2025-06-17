package org.crain.dme4j.core.types;

/**
 * {@link Value}
 */
public final class DoubleValue extends Value<Double> {

    /**
     * Default value is 0.0
     */
    public DoubleValue() {
        super(0.0, 16);
    }

    public DoubleValue(Double value) {
        super(value, 16);
    }
}
