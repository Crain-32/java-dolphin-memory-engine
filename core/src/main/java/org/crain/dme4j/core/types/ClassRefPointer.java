package org.crain.dme4j.core.types;

import java.util.Objects;

/**
 * A Pointer is best viewed in the exact same way as normal pointers (2 memory regions, source/target), with the
 * exception that we allow for a cached inner value on the Type for performance. If we wanted to model more
 * traditional models, we'd store the MemoryType's Class value and the underlying.
 * That actually isn't a bad idea
 * <br>
 * Default behavior for a {@link ClassRefPointer} is "Lazy", and the next usage will not read nor write with Memory.
 *
 * @param <MType> The {@link MemoryType} this Pointer references.
 */
public final class ClassRefPointer<MType extends MemoryType> implements PointerType {
    private final Class<MType> clazzRef;
    private MType innerValue;
    private long pointerValue;
    // We retrieve the underlying value when we read the address if followPointer || isEager
    private boolean followPointer = false;
    // FetchType.EAGER
    private boolean isEager = false;

    /**
     * Creates a new lazy Pointer this an address of 0.
     *
     * @param value Non-null {@link MemoryType} class this pointer will point to.
     * @throws NullPointerException If the provided value is null.
     */
    public ClassRefPointer(final MType value) {
        Objects.requireNonNull(value, "The Provided value cannot be null.");
        this.innerValue = value;
        this.clazzRef = (Class<MType>) value.getClass();
    }

    /**
     * Creates a new lazy Pointer with the provided Address.
     *
     * @param value        Non-null {@link MemoryType} class this pointer will point to.
     * @param pointerValue Native Memory address of the pointer. Cannot be negative
     * @throws IllegalArgumentException If the provided value or Address is non-compliant.
     * @throws NullPointerException if the provided value is null.
     */
    public ClassRefPointer(final MType value, final long pointerValue) throws IllegalArgumentException {
        if (pointerValue < 0) throw new IllegalArgumentException("The pointer address cannot be negative.");
        this.pointerValue = pointerValue;
        this(value);
    }

    /**
     * I <em>feel</em> like there is a valid usecase and reason for this. I do not know what it is yet.
     */
    public ClassRefPointer(Class<MType> clazzRef) {
        Objects.requireNonNull(clazzRef, "The Provided Class<?> cannot be null.");
        this.clazzRef = clazzRef;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPointerAddress(final long pointerAddress) {
        this.pointerValue = pointerAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getPointerAddress() {
        return pointerValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean pointsNull() {
        return pointerValue == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void eager(boolean eager) {
        this.isEager = eager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEager() {
        return isEager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shouldFollowPointer(boolean followPointer) {
        this.followPointer = followPointer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean willFollowPointer() {
        return isEager || followPointer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearFollow() {
        this.followPointer = false;
    }

    /**
     * True if this pointer is nesting another Pointer, for example {@literal Pointer<StringPointer>}
     */
    @SuppressWarnings("unused")
    public boolean pointyPointer() {
        return PointerType.class.isAssignableFrom(clazzRef);
    }

    /**
     * The value of the MemoryType at the target address.
     */
    public MType getValue() {
        return innerValue;
    }

    /**
     * @param value A non-null value that is assignable to the {@code clazzRef} for the instance.
     */
    public void setValue(MType value) {
        Objects.requireNonNull(value, "The Provided value cannot be null.");
        if (!clazzRef.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("""
                    The provided value %s is not assignable to %s, this is either a bug, or a reflection bypass.
                    """.formatted(value.getClass().getCanonicalName(), clazzRef.getCanonicalName()));
        }
        this.innerValue = value;
    }

    /**
     * Convenience method for creating a default Pointer around a provided Memory Type.
     *
     * @param instance Non-Null Memory Type to create a pointer for.
     * @return A new Pointer with a native address of 0.
     */
    public static <MType extends MemoryType> ClassRefPointer<MType> atNull(MType instance) {
        return new ClassRefPointer<>(instance);
    }

    /**
     * Convenience method for creating a default Pointer around a provided Memory Type.
     *
     * @param instance Non-Null Memory Type Class to create a Pointer for.
     * @return A new Pointer with a native address of 0.
     */
    public static <MType extends MemoryType> ClassRefPointer<MType> atNull(Class<MType> instance) {
        return new ClassRefPointer<>(instance);
    }

    /**
     * Subject to removal
     */
    @SuppressWarnings("unused")
    public long getOffset(String fieldOffset) {
        if (innerValue instanceof Struct st) {
            return pointerValue + st.structOffset(fieldOffset);
        } else {
            return pointerValue;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return Due to the nature of Pointers, this value is always 4.
     */
    @Override
    public int getSize() {
        return PointerType.getPointerSize();
    }

    /**
     * {@inheritDoc}
     *
     * @return The inner type's Classname.
     */
    @Override
    public String getRepresentation() {
        return "Pointer for Type: " + (
                innerValue.getClass().equals(clazzRef) ?
                        clazzRef.getSimpleName() :
                        innerValue.getClass().getSimpleName()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<MType> pointsAt() {
        return clazzRef;
    }

}
