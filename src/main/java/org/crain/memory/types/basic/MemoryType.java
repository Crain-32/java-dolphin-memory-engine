package org.crain.memory.types.basic;

public sealed interface MemoryType permits PointerType, Struct, Value {

    int getSize();

    String getRepresentation();
}
