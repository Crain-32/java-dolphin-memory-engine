package org.crain.memory.types;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.StringJoiner;

public final class Array<T extends MemoryType> implements MemoryType {

    private int elementSize;
    private T[] elements;
    private final boolean forPointers;

    public Array(final T[] elements) {
        this.elements = elements;
        elementSize = Arrays.stream(elements).mapToInt(MemoryType::getSize).max().orElseThrow();
        forPointers = Arrays.stream(elements).anyMatch(MemoryType::isPointer);
    }

    public T getAtIndex(final int index) {
        return elements[index];
    }

    public T getAtIndex(final String jsonEscString) {
        if (StringUtils.isBlank(jsonEscString)) throw new IllegalArgumentException("Input cannot be null or empty");
        return getAtIndex(
                Integer.parseInt(
                        jsonEscString.substring(jsonEscString.lastIndexOf("["),
                                jsonEscString.lastIndexOf("]")
                        )
                )
        );
    }

    public void makeAllEager(final boolean eager) {
        if (!isForPointers()) return;
        for (T t : elements) {
            var element = (PointerType) t;
            element.eager(eager);
        }
    }

    public int getOffset(final String jsonEscString) {
        if (StringUtils.isBlank(jsonEscString)) throw new IllegalArgumentException("Input cannot be null or empty");
        if (!jsonEscString.contains(".")) return getOffsetForIndex(jsonEscString);
        return OffsetUtil.getArrayOffset(this, jsonEscString);
    }

    public boolean isForPointers() {
        return forPointers;
    }

    public int getOffsetForIndex(final int index) {
        return elementSize * index;
    }

    public int getLength() {
        return elements.length;
    }

    public int getOffsetForIndex(final String jsonEscString) {
        if (StringUtils.isBlank(jsonEscString)) throw new IllegalArgumentException("Input cannot be null or empty");
        return getOffsetForIndex(
                Integer.parseInt(
                        jsonEscString.substring(jsonEscString.lastIndexOf("["),
                                jsonEscString.lastIndexOf("]")
                        )
                )
        );
    }

    @Override
    public int getSize() {
        return elementSize * elements.length;
    }

    @Override
    public String getRepresentation() {
        StringJoiner joiner = new StringJoiner("\n");
        for (T element : elements) {
            String representation = element.getRepresentation();
            joiner.add(representation);
        }
        return "[\n" + joiner + "]";
    }
}
