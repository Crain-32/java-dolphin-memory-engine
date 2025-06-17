package org.crain.dme4j.core.types;

import java.util.function.Function;

/**
 * You can think of this as a reflection of primitives. Although char isn't in here.
 * Should eventually be using the JDK's Value Class? Or be adjusted to account for it?
 * <br>
 * A Null inner value is assumed to be "0", although that might be hard to handle...
 * @param <V> Primitive value this maps to. Assured to be Primitive due to the class Sealing
 */
public abstract sealed class Value<V> implements MemoryType permits
        ByteValue, DoubleValue, FloatValue, IntValue, LongValue, ShortValue, PaddingValue {

    private V value;
    private int size;
    private Function<V, String> stringTransformer = Object::toString;

    /**
     * @param value Underlying Value we're wrapping
     * @param size Size of that value, must be positive
     */
    protected Value(V value, int size) {
        this.value = value;
        // Since we've sealed the class, we know this is always positive.
        this.size = size;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public V getValue() {
        return value;
    }

    public int getSize() {
        return size;
    }

    public void setStringTransformer(Function<V, String> stringTransformer) {
        this.stringTransformer = stringTransformer;
    }
    public void resetStringTransformer() {
        stringTransformer = Object::toString;
    }

    @Override
    public String getRepresentation() {
        return stringTransformer.apply(value);
    }
}
