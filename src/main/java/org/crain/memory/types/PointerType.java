package org.crain.memory.types;

public sealed interface PointerType extends MemoryType permits StringPointer, Pointer {

    void setPointerAddress(final long pointerAddress);
    long getPointerAddress();

    boolean pointsNull();
    void eager(final boolean eager);
    default void makeEager() {
        eager(true);
    }
    boolean isEager();
    void shouldFollowPointer(final boolean followPointer);
    boolean willFollowPointer();
    void clearFollow();

}
