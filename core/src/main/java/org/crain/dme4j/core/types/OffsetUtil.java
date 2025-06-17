package org.crain.dme4j.core.types;

import org.crain.dme4j.core.log.LoggingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Private Utility to ensure helper functions aren't in the MemoryType class definition, even if they could be
 * package-private. Just reduces the noise on the class definition.
 */
class OffsetUtil {
    private static final Logger log = LoggerFactory.getLogger(OffsetUtil.class);

    private OffsetUtil() {
        throw new IllegalCallerException("Utility class");
    }

    /**
     * Structs maintain an internal offset map, where Arrays use MemoryType.size * index + Struct offset call
     *
     * @return Offset of the key value from some relative point of the MemoryType.
     */
    static int getMemoryTypeOffset(MemoryType type, String key) {
        log.atDebug().setMessage("Getting offset for MemoryType: {} with the Key {}")
                .addArgument(() -> type.getClass().getSimpleName() + (log.isTraceEnabled() ? ":" + System.identityHashCode(type) : ""))
                .addArgument(key)
                .log();

        return switch (type) {
            case Struct struct -> struct.structOffset(key);
            case Array<?> a -> a.getOffset(key);
            case PointerType _, Value<?> _ -> 0;
        };
    }

    // :/ Unsure how to handle all this. Might extract the Query String into another module? TBD

    /**
     * When handling a Struct offset, we have to be watchful for composition.
     * We define composite access as "a.b.c", so we'd check the offset of A, then add the offset of B, then the offset of C.
     * This gives us the offset of a.b.c from the root of A.
     *
     * @param struct Struct that is acting as the root to calculate the offset.
     * @param key    Access Key, potentially nested or otherwise defined.
     * @return Offset from the root Struct provided.
     * @throws IllegalArgumentException if the key resolves to a leaf of the struct that isn't a Struct.
     */
    static int getStructOffset(Struct struct, String key) {
        log.atDebug().setMessage("Getting offset for Struct: {} with the Key {}")
                .addArgument(() -> LoggingUtil.ClassBasedOnLogLevel(struct, log))
                .addArgument(key)
                .log();

        if (!key.contains(".")) {
            log.atDebug().setMessage("Getting Direct Offset from Struct")
                    .log();

            return struct.structOffset(key);
        }
        var splitOffset = key.split("\\.", 2);
        MemoryType innerStruct;
        String arrayKey;
        if (splitOffset[0].contains("[")) {
            log.trace("Key referenced an Array");

            arrayKey = splitOffset[0].substring(
                    0, splitOffset[0].lastIndexOf("[")
            );
            log.atTrace().setMessage("Array Key: {}").addArgument(arrayKey).log();
            innerStruct = struct.checkYourselfValue(arrayKey);
        } else {
            arrayKey = null;
            innerStruct = struct.checkYourselfValue(splitOffset[0]);
        }
        return switch (innerStruct) { // I need to really ensure this is tested for my own sanity.
            case Struct s -> s.structOffset(splitOffset[1]);
            case Array<?> a when arrayKey != null ->
                    struct.structOffset(arrayKey) + a.getOffsetForIndex(splitOffset[0]) +
                    getMemoryTypeOffset(a.getAtIndex(splitOffset[0]), splitOffset[1]);
            case null -> throw new IllegalStateException("Attempted to request non-struct field: " + splitOffset[0]);
            default -> 0; // Throw exception? Really depends on if we expect an invalid type
        };
    }

    // Adjust into proper Lexer/Parser for learning at some point. Reference making an Interpreter in Go.
    static int getArrayOffset(Array<?> array, String key) {
        log.atDebug().setMessage("Getting offset for Array: {} with the Key {}")
                .addArgument(() -> LoggingUtil.ClassBasedOnLogLevel(array, log))
                .addArgument(key)
                .log();

        if (array.isForPointers()) {
            return array.getOffsetForIndex(key);
        } else {
            var splitKey = key.split("\\.", 2);
            return array.getOffsetForIndex(splitKey[0]) + getMemoryTypeOffset(array.getAtIndex(splitKey[0]), splitKey[1]);
        }
    }
}
