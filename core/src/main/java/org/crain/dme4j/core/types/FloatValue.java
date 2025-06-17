package org.crain.dme4j.core.types;

/**
 * {@link Value}
 */
public final class FloatValue extends Value<Float> {

    /**
     * Default value is 0.0f
     */
    public FloatValue() {
        super(0.0f, 4);
    }

    public FloatValue(Float value) {
        super(value, 4);
    }
}
