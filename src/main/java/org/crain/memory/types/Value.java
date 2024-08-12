package org.crain.memory.types;

import java.util.function.Function;

public abstract sealed class Value<T> implements MemoryType permits
        ByteValue, DoubleValue, FloatValue, IntValue, LongValue, ShortValue, PaddingValue {

    private T value;
    private int size;
    private Function<T, String> stringTransformer = Object::toString;



    protected Value(T value) {
        this.value = value;
    }

    protected Value(T value, int size) {
        this.value = value;
        this.size = size;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public int getSize() {
        return size;
    }

    public void setStringTransformer(Function<T, String> stringTransformer) {
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
