package org.crain.memory.types;

class OffsetUtil {
    private OffsetUtil() {
        throw new IllegalCallerException("Utility class");
    }

    static int getMemoryTypeOffset(MemoryType type, String key) {
        return switch(type) {
            case Struct struct -> struct.structOffset(key);
            case Array<?> a -> a.getOffset(key);
            case PointerType _, Value<?> _ -> 0;
        };
    }

    static int getStructOffset(Struct struct, String key) {
        if (key.contains(".")) {
            var splitOffset = key.split("\\.", 2);
            MemoryType innerStruct;
            String arrayStr;
            if (splitOffset[0].contains("[")) {
                arrayStr = splitOffset[0].substring(
                        0, splitOffset[0].lastIndexOf("[")
                );
                innerStruct = struct.getYourselfValue(arrayStr);
            } else {
                arrayStr = null;
                innerStruct = struct.getYourselfValue(splitOffset[0]);
            }
            return switch (innerStruct) {
                case Struct s -> s.structOffset(splitOffset[1]);
                case Array<?> a when arrayStr != null ->
                        struct.structOffset(arrayStr) + a.getOffsetForIndex(splitOffset[0]) +
                        getMemoryTypeOffset(a.getAtIndex(splitOffset[0]), splitOffset[1]);
                case null ->
                        throw new IllegalStateException("Attempted to request non-struct field: " + splitOffset[0]);
                default -> 0;
            };
        }
        return struct.structOffset(key);
    }

    static int getArrayOffset(Array<?> array, String key) {
        if (array.isForPointers()) {
            return array.getOffsetForIndex(key);
        } else {
            var splitKey = key.split("\\.", 2);
            return array.getOffsetForIndex(splitKey[0]) + getMemoryTypeOffset(array.getAtIndex(splitKey[0]), splitKey[1]);
        }
    }
}
