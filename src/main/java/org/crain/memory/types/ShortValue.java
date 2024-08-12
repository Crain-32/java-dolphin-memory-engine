package org.crain.memory.types;

public final class ShortValue extends Value<Short> {
    public ShortValue() {
        super ((short) 0,2 );
    }
    public ShortValue(Short value) {
        super(value, 2);
    }
}
