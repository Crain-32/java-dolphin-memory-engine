package org.crain.dme4j.core.types;

import org.crain.dme4j.core.log.LoggingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Array Extension of {@link MemoryType}. This handles the Array Access, Struct vs Primitive Arrays, offset management.
 */
public final class Array<MType extends MemoryType> implements MemoryType {

    private final static Logger log = LoggerFactory.getLogger(Array.class.getName());

    private int elementSize;
    private MType[] elements;
    private final boolean forPointers;
    private final Class<?> elementType;

    /**
     * Restrictive constructor for Arrays. The provided Array is validated against {@link Array#validateStruct(MemoryType[])}
     * @param elements Base Array to wrap for.
     */
    public Array(final MType[] elements) {
        validateStruct(elements);

        this.elements = elements;
        elementSize = Arrays.stream(elements).mapToInt(MemoryType::getSize).max().orElseThrow();
        forPointers = Arrays.stream(elements).anyMatch(MemoryType::isPointer);
        elementType = elements[0].getClass();
        log.atDebug().setMessage("Array created for type {}, elements are of size {}, and forPointers {}")
                .addArgument(() -> elements[0].getClass().getCanonicalName())
                .addArgument(elementSize)
                .addArgument(forPointers)
                .log();
    }

    //TODO Should validate the elements are all of the same typed Struct, or type Pointer, or type Value
    /**
     * Validates the Java Array's Structure in line with the requirements of {@link Array}.
     * @param elements Java Array to check against.
     * @throws IllegalArgumentException If the Java Array is empty.
     * @throws NullPointerException If the Java Array, or any elements in it, are null.
     */
    void validateStruct(final MType[] elements) throws IllegalArgumentException, NullPointerException {
        Objects.requireNonNull(elements, "The provided Array must not be null");
        if (elements.length == 0) { throw new IllegalArgumentException("The provided Array must contain at least one element"); }
        for (int i = 0; i < elements.length; i++) {
            final int index = i;
            Objects.requireNonNull(elements[i], () -> "The provided element at index " + index + " was null.");
        }
    }

    /**
     * Returns the {@link MemoryType} at the provided Index
     * @param index Non-null index to check for, we utilize Java's Array Checks for this.
     * @return A Memory Type at the index.
     * @throws IndexOutOfBoundsException in alignment with Java Arrays
     */
    public MType getAtIndex(final int index) throws IndexOutOfBoundsException {
        log.atTrace().setMessage("Retrieving index {} for Array {}")
                .addArgument(index)
                .addArgument(() -> System.identityHashCode(this)).log();

        return elements[index];
    }

    /**
     * Returns the {@link MemoryType} at the provided Index in the String
     * @param jsonEscString String in the format [{@literal <Index>}]
     * @return The {@link MemoryType} at that index.
     * @throws NullPointerException if the {@code jsonEscString} is null.
     * @throws IllegalArgumentException if the {@code jsonEscString} is blank.
     */
    public MType getAtIndex(final String jsonEscString) throws IllegalArgumentException, NullPointerException {

        Objects.requireNonNull(jsonEscString, "Input cannot be null");
        if (jsonEscString.isBlank()) throw new IllegalArgumentException("Input cannot be empty");

        log.atDebug()
                .setMessage("Retrieving with String {} for Array {}")
                .addArgument(jsonEscString)
                .addArgument(System.identityHashCode(this)).log();

        return getAtIndex(
                Integer.parseInt(
                        jsonEscString.substring(
                                jsonEscString.lastIndexOf("["),
                                jsonEscString.lastIndexOf("]")
                        )
                )
        );
    }

    /**
     * Converts all {@link PointerType PointerTypes} in the array into either lazy or eager pointers.
     * @param eager Convert all to Eager Pointers if True, else Lazy
     */
    public void makeAllEager(final boolean eager) {
        log.atTrace()
                .setMessage("Setting all in Array {} to {}")
                .addArgument(() -> LoggingUtil.ClassBasedOnLogLevel(this, log))
                .addArgument(() -> eager ? "Eager" : "Lazy")
                .log();

        if (!isForPointers()) return;
        for (MType mType : elements) {
            var element = (PointerType) mType;
            element.eager(eager);
        }
    }

    /**
     * Retrieves the Offset for a field or index for a Json-Path esq String.
     * @param jsonEscString Json-Path Styled String to base access on.
     * @return offset of the target within the array.
     * @throws NullPointerException if the {@code jsonEscString} is null.
     * @throws IllegalArgumentException if the {@code jsonEscString} is Blank.
     */
    public int getOffset(final String jsonEscString) throws IllegalArgumentException, NullPointerException {
        Objects.requireNonNull(jsonEscString, "Input cannot be null");
        if (jsonEscString.isBlank()) throw new IllegalArgumentException("Input cannot be empty");
        if (!jsonEscString.contains(".")) return getOffsetForIndex(jsonEscString);
        return OffsetUtil.getArrayOffset(this, jsonEscString);
    }

    /**
     * Returns if the {@link Array} contains pointers.
     * @return True if the elements are {@link PointerType PointerTypes}.
     */
    public boolean isForPointers() {
        return forPointers;
    }

    /**
     * Retrieves the offset for an index based on the {@link Array#elementSize}
     * @param index positive index to base retrieval on.
     * @return offset of the index for this array.
     * @throws IllegalArgumentException if the provided index is negative or out of bounds.
     */
    public int getOffsetForIndex(final int index) throws IllegalArgumentException{
        if (index < 0 || index >= getLength()) {
            throw new IllegalArgumentException("Index " + index + " is out of bounds for an Array sized " + getLength());
        }
        return elementSize * index;
    }

    /**
     * @return the length of the underlying array.
     */
    public int getLength() {
        // Note that {@link Array#getSize()} already exists, so we can't mirror List-style method names.
        return elements.length;
    }

    /**
     * Retrieves the offset of an Index, mirroring the validation and expectations of {@link Array#getAtIndex(String)}
     */
    public int getOffsetForIndex(final String jsonEscString) {
        Objects.requireNonNull(jsonEscString, "Input cannot be null");
        if (jsonEscString.isBlank()) throw new IllegalArgumentException("Input cannot be empty");

        return getOffsetForIndex(
                Integer.parseInt(
                        jsonEscString.substring(
                                jsonEscString.lastIndexOf("["),
                                jsonEscString.lastIndexOf("]")
                        )
                )
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return elementSize * elements.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRepresentation() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("[");
        for (MType mType : elements) {
            String representation = mType.getRepresentation();
            joiner.add(representation);
        }
        joiner.add("]");
        return joiner.toString();
    }

    /**
     * Returns the Class this Array Represents.
     */
    public Class<MType> getElementType() {
        return (Class<MType>) elementType;
    }
}
