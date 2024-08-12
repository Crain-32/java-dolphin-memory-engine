package org.crain.memory.types;

public final class IntValue extends Value<Integer> {
    public IntValue() {
        super(0, 4);
    }
    public IntValue(Integer value) {
        super(value, 4);
    }
}
