package org.crain.memory.types.basic;

public final class LongValue extends Value<Long> {
    public LongValue() {
        super(0L,0);
    }
    public LongValue(Long value) {
        super(value, 8);
    }
}
