package org.crain.memory.types.basic;

public sealed interface PointerType extends MemoryType permits StringPointer, Pointer {

    void setPointerAddress(final long pointerAddress);
    long getPointerAddress();

    boolean pointsNull();
}
