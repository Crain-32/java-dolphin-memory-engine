package org.crain.dme4j.core.types;

// How do we do a deferred operation? if not isEager, then we could do a lazy approach, but that requires an engine
// reference inside of the Pointer. A functional closure *could* work, but I'm unsure if we'd want that without
// some sort of clearing operation, along this doesn't pertain to Strings.

/**
 * {@link PointerType} is the branch of {@link MemoryType} that handles... Pointers.....
 * Pointers can be "Eager" or "Lazy", with the behavior of that defined at {@link PointerType#eager(boolean)}
 *
 */
public sealed interface PointerType extends MemoryType permits StringPointer, ClassRefPointer {

    /**
     * Sets the GameCube Memory Address of the {@link PointerType}
     * @param pointerAddress Positive value to set the Pointer to.
     */
    void setPointerAddress(final long pointerAddress);

    /**
     * Retrieves the address for this {@link PointerType}, potentially 0 if this is {@link PointerType} is not
     * associated to the GameCube Memory.
     * @return Non-negative value of the GameCube Memory Address
     */
    long getPointerAddress();

    /**
     * True if {@link PointerType#getPointerAddress()} is 0, otherwise false
     */
    boolean pointsNull();

    /**
     * An "Eager" {@link PointerType} has the following traits
     * <ul>
     *     <li>If this Object is part of a query, the Engine should return the full value this points to</li>
     *     <li>The return value of {@link PointerType#getPointerAddress()} will never be Zero after the first query.</li>
     *     <li>If used in a writing context, this Pointer will be written to Memory.</li>
     * </ul>
     * "Lazy" pointers have the opposite properties, but that isn't a hard requirement for them.
     * @param eager Sets this {@link PointerType} to "Eager" if True, otherwise it will be set to Lazy.
     */
    void eager(final boolean eager);

    /**
     * Calls {@link PointerType#eager(boolean)} with the value of True, convenience function for Functional Syntax.
     */
    @SuppressWarnings("unused")
    default void makeEager() {
        eager(true);
    }

    /**
     * True if this {@link PointerType} is "Eager", otherwise false.
     */
    @SuppressWarnings("unused")
    boolean isEager();

    /**
     * Used to control query behavior in cases where the Pointer is "Lazy" but we need the data still.
     * If true, the next query will make sure to populate or write from this {@link PointerType}
     * @param followPointer True if this {@link PointerType} should read or write to Memory
     */
    void shouldFollowPointer(final boolean followPointer);

    /**
     * True if this Pointer is "Eager" OR the last value to {@link PointerType#shouldFollowPointer(boolean)} was True.
     */
    boolean willFollowPointer();

    /**
     * Behavior should mirror calling {@link PointerType#shouldFollowPointer(boolean)} with false.
     */
    default void clearFollow(){
        shouldFollowPointer(false);
    }

    static int getPointerSize() {
        return 4;
    }

    /**
     * Returns the underlying type this pointer points at.
     */
    Class<?> pointsAt();

    // Struct A -> Struct B, Instance of B -> Struct A infinite loop
}
