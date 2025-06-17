package org.crain.dme4j.core.types;

import java.util.Objects;


/**
 * Simple Utility class that allows for a padding to be within a {@link Struct}
 * {@link Value}
 */
public final class PaddingValue extends Value<Byte[]> {


    /**
     * Creates a new {@link PaddingValue} wrapping the provided Byte[]
     * @param value The Byte[] to wrap, note that this value isn't read to/from
     * @throws NullPointerException if {@code value == null}
     * @throws IllegalArgumentException if {@code value.length == 0}
     */
    public PaddingValue(final Byte[] value) {
        Objects.requireNonNull(value, "Value must not be null");
        if (value.length == 0) throw new IllegalArgumentException();
        super(value, value.length);
    }

    /**
     * Creates a new {@link PaddingValue} of length {@code length}
     * @param length Length of the padding
     * @throws IllegalArgumentException if {@code length <= 0}
     */
    public PaddingValue(final int length) {
        if (length <= 0) throw new IllegalArgumentException("Provided Length is empty");
        super(new Byte[length], length);
    }


    /**
     * Mirrors - {@link PaddingValue#PaddingValue(Byte[])}
     */
    public static PaddingValue of(final Byte[] value) {
        return new PaddingValue(value);
    }

    /**
     * Mirrors - {@link PaddingValue#PaddingValue(int)}
     */
    public static PaddingValue of(final int length) {
        return new PaddingValue(length);
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException if the new array is of a different length.
     */
    @Override
    public void setValue(Byte[] value) {
        if (value.length != getSize()) {
            throw new IllegalArgumentException("Mismatched byte array size");
        }
        // We don't actually set any values in Padding.
    }

    /**
     * Mirrors {@link PaddingValue#setValue(Byte[])}
     */
    public void setValue(byte[] value) {
        if (value.length != getSize()) {
            throw new IllegalArgumentException("Mismatched byte array size");
        }
        // We don't actually set any values in Padding.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRepresentation() {
        return "Padding of Length: " + getSize();
    }
}
