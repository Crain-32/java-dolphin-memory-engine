package org.crain.memory.types.basic;

public abstract sealed class Value<T> implements MemoryType permits
        ByteValue, DoubleValue, FloatValue, IntValue, LongValue, ShortValue, PaddingValue {

    private T value;
    private int size;


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
    @Override
    public String getRepresentation() {
        return getValue().toString();
    }
}
