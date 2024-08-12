package org.crain.memory.types;

public sealed interface MemoryType permits PointerType, Struct, Array, Value {

    int getSize();

    String getRepresentation();

    default boolean isPointer() {
        return this instanceof PointerType;
    }
}
