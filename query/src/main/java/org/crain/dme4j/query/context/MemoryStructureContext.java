package org.crain.dme4j.query.context;

import org.crain.dme4j.core.types.ClassRefPointer;
import org.crain.dme4j.core.types.MemoryType;
import org.crain.dme4j.core.types.PointerType;
import org.crain.dme4j.core.types.StringPointer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryStructureContext {
    private Map<String, ClassRefPointer<?>> aliases;
    private ClassRefPointer<?> root;

    private MemoryStructureContext(ClassRefPointer<?> root) {
        this.root = root;
        this.aliases = new ConcurrentHashMap<>();
    }
    public static MemoryStructureContext withRoot(PointerType root) {
        return switch (root) {
            case StringPointer _ -> throw new IllegalArgumentException("Strings Cannot be the Root Context");
            case ClassRefPointer<?> classRefPointer when classRefPointer.pointsNull() -> throw new IllegalArgumentException("Pointers Cannot be null");
            case ClassRefPointer<?> classRefPointer -> new MemoryStructureContext(classRefPointer);
            case null -> throw new IllegalArgumentException("Pointers Cannot be null");
        };
    }

    public static MemoryStructureContext withRoot(long consoleAddress, MemoryType root) {
        return switch (root) {
            case StringPointer _ -> throw new IllegalArgumentException("Strings Cannot be the Root Context");
            case ClassRefPointer<?> classRefPointer when classRefPointer.pointsNull() -> {
                classRefPointer.setPointerAddress(consoleAddress);
                yield withRoot(classRefPointer);
            }
            case ClassRefPointer<?> classRefPointer -> withRoot(classRefPointer);
            case MemoryType memoryType -> withRoot(new ClassRefPointer<>(memoryType, consoleAddress));
            case null -> throw new IllegalArgumentException("Context requires a non-null root");
        };
    }
}
