package org.crain.memory.types.basic;

import java.util.*;

public non-sealed class Struct implements MemoryType {
    private SequencedMap<String, MemoryType> structValues;
    private SequencedMap<String, Integer> structOffsets;
    private boolean locked = false;
    private int finalSize = 0;

    protected Struct() {
        structValues = new LinkedHashMap<>();
        structOffsets = new LinkedHashMap<>();
    }

    protected void registerValue(final String key, final MemoryType value) {
        if (locked) throw new IllegalStateException("Attempted to register a value for a Locked Struct");
        Objects.requireNonNull(key);
        if (structValues.containsKey(key)) throw new IllegalStateException("Attempted to re-register a Struct Field");
        Objects.requireNonNull(value);
        structOffsets.put(key, getSize());
        structValues.put(key, value);
    }
    protected void registerPadding(final int length) {
        if (locked) throw new IllegalStateException("Attempted to register Padding for a Locked Struct");
        if (length <= 0) throw new IllegalArgumentException("Length must be positive");
        registerValue("Padding" + structValues.size(), new PaddingValue(length));
    }

    public void lockStruct() {
        finalSize = getSize();
        locked = true;
    }

    public boolean isLocked() {
        return locked;
    }

    public int getSize() {
        if (locked) {
            return finalSize;
        } else {
            return structValues.sequencedValues().stream().mapToInt(MemoryType::getSize).sum();
        }
    }

    /**
     * If you want to roll the dice, you can roll the dice
     */
    @SuppressWarnings("unchecked")
    public <T extends MemoryType> T getUncheckedValue(String key) {
        return (T) structValues.getOrDefault(key, null);
    }

    public MemoryType getYourselfValue(String key) {
        return structValues.getOrDefault(key, null);
    }

    public SequencedSet<String> structFieldNames() {
        return structValues.sequencedKeySet();
    }

    public int structOffset(String offset) {
        return structOffsets.getOrDefault(offset, 0);
    }

    @Override
    public String getRepresentation() {
        return "Struct Value";
    }

    public List<String> getFieldRepresentation() {
        return structFieldNames().stream()
                .map(fieldName -> {
                    MemoryType fieldType = getUncheckedValue(fieldName);
                    return fieldName + ": " + fieldType.getRepresentation() + " : " + fieldType.getSize();
                }).toList();
    }
}
