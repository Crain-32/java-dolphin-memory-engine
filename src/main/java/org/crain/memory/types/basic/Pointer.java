package org.crain.memory.types.basic;

public final class Pointer<T extends MemoryType> implements PointerType {
    private T innerValue;
    private long pointerValue;

    public Pointer(final T value) {
        if (value == null) throw new IllegalStateException("The Provided value cannot be null.");
        this.innerValue = value;
    }

    public Pointer(final T value, final long pointerValue) {
        if (value == null) throw new IllegalStateException("The Provided value cannot be null.");
        this.innerValue = value;
        this.pointerValue = pointerValue;
    }

    @Override
    public void setPointerAddress(final long pointerAddress) {
        this.pointerValue = pointerAddress;
    }

    @Override
    public boolean pointsNull() {
        return pointerValue == 0;
    }

    public long getPointerAddress() {
        return pointerValue;
    }

    public T getValue() {
        return innerValue;
    }

    public static <T extends MemoryType> Pointer<T> ofNull(T instance) {
        return new Pointer<>(instance);
    }

    public long getOffset(String fieldOffset) {
        if (innerValue instanceof Struct st) {
            return pointerValue + st.structOffset(fieldOffset);
        } else {
            return pointerValue;
        }
    }

    @Override
    public int getSize() {
        return 4;
    }

    @Override
    public String getRepresentation() {
        return "Pointer for Type: " + innerValue.getClass().getSimpleName();
    }
}
