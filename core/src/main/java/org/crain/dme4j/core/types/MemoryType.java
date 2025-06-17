package org.crain.dme4j.core.types;

/**
 * A MemoryType is best viewed as the core metadata attached to a Type. It doesn't define any sort of mapping
 * between Native/Java memory, but instead the size of memory required to map to native, if the type is a pointer,
 * and a String representation of the type, mainly for debugging purposes.
 * <br/>
 * We break it down into the following types to allow the framework to focus on the following
 * <ul>
 *     <li>Access Optimization. Knowing if something has a pointer or the size of it is key.</li>
 *     <li>Simplification of the Abstraction. The consumer is mostly interested in access and mapping, not pipeline for that.</li>
 *     <li>In lieu of the above, we also can reference more internal classes this way, which makes our side less abstract.</li>
 * </ul>
 */
public sealed interface MemoryType permits PointerType, Struct, Array, Value {

    /**
     * Size of the object in Gamecube memory space, in bytes.
     * For example a pointer would return 4.
     * @return Gamecube Memory size, in Bytes
     */
    int getSize();

    /**
     * Used for logging/debug purposes. It isn't recommended to rely on this function to be cross-version stable.
     * The only part this should respect is that any implements should <em>not</em> end with new lines or whitespace.
     * @return Readable String of the Type.
     */
    String getRepresentation();

    /**
     * Syntax sugar to allow for type.isPointer() instead of type instanceof PointerType in parts of the code.
     */
    default boolean isPointer() {
        return this instanceof PointerType;
    }
}
