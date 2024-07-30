package org.crain.memory.types.basic;

public final class ByteValue extends Value<Byte> {
    public ByteValue() {
        super((byte) 0, 1);
    }

    public ByteValue(Byte value) {
        super(value, 1);
    }
}
