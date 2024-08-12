package org.crain.memory.engine;

import org.crain.memory.types.MemoryType;
import org.crain.memory.types.PointerType;

public interface GamecubeMemoryEngine {

    boolean connect();

    boolean getStatus();

    byte[] readFromRAM(final long consoleAddress, final int size);

    boolean writeToRAM(final long consoleAddress, byte[] val);

    boolean readIntoMemoryType(final long consoleAddress, MemoryType memoryType);

    default boolean readIntoPointerType(PointerType pointerType) {
        return readIntoMemoryType(pointerType.getPointerAddress(), pointerType);
    }

    boolean writeFromMemoryType(final long consoleAddress, MemoryType memoryType);

    default boolean writeIntoPointerType(PointerType pointerType) {
        return writeFromMemoryType(pointerType.getPointerAddress(), pointerType);
    }
}
