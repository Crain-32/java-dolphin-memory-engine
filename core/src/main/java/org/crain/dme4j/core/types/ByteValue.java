package org.crain.dme4j.core.types;

/**
 * {@link Value}
 */
public final class ByteValue extends Value<Byte> {

    /**
     * Default value of 0
     */
    public ByteValue() {
        super((byte) 0, 1);
    }

    /**
     * @param value Underlying Value this wraps.
     */
    public ByteValue(Byte value) {
        super(value, 1);
    }

}
