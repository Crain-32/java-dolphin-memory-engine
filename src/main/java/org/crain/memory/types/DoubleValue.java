package org.crain.memory.types;

public final class DoubleValue extends Value<Double> {

    public DoubleValue() {
        super(0.0, 16);
    }

    public DoubleValue(Double value) {
        super(value, 16);
    }
}
