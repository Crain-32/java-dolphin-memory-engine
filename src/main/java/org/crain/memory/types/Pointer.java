package org.crain.memory.types;

public final class Pointer<T extends MemoryType> implements PointerType {
    private T innerValue;
    private long pointerValue;
    private static final int POINTER_SIZE = 4;
    private boolean followPointer = false;
    private boolean isEager = false;

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

    @Override
    public void eager(boolean eager) {
        this.isEager = eager;
    }
    @Override
    public boolean isEager() {
        return isEager;
    }

    @Override
    public void shouldFollowPointer(boolean followPointer) {
        this.followPointer = followPointer;
    }

    @Override
    public boolean willFollowPointer() {
        return isEager || followPointer;
    }
    @Override
    public void clearFollow() {
        this.followPointer = false;
    }

    public long getPointerAddress() {
        return pointerValue;
    }

    public T getValue() {
        return innerValue;
    }

    public static <T extends MemoryType> Pointer<T> atNull(T instance) {
        return new Pointer<>(instance);
    }

    public static int pointerSize() {
        return POINTER_SIZE;
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
        return POINTER_SIZE;
    }

    @Override
    public String getRepresentation() {
        return "Pointer for Type: " + innerValue.getClass().getSimpleName();
    }
}
