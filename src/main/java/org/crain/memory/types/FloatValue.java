package org.crain.memory.types;

public final class FloatValue extends Value<Float> {
    public FloatValue() {
        super(0.0f, 4);
    }
    public FloatValue(Float value) {
        super(value, 4);
    }
}
