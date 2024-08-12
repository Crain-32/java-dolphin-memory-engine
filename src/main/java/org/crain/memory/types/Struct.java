package org.crain.memory.types;

import java.util.*;

public non-sealed class Struct implements MemoryType {
    private SequencedMap<String, MemoryType> structValues;
    private SequencedMap<String, Integer> structOffsets;
    private boolean locked = false;
    private int finalSize = 0;
    private int paddingFieldCount = 0;

    protected Struct() {
        structValues = new LinkedHashMap<>();
        structOffsets = new LinkedHashMap<>();
    }

    protected void registerValue(final String key, final Value<?> value) {
        if (locked) throw new IllegalStateException("Attempted to register a value for a Locked Struct");
        Objects.requireNonNull(key);
        if (key.contains(".")) throw new IllegalStateException("Attempted to register a field with a nested key");
        if (structValues.containsKey(key))
            throw new IllegalStateException("Attempted to re-register the Field: " + key);
        Objects.requireNonNull(value);
        structOffsets.put(key, getSize());
        structValues.put(key, value);
    }

    protected void registerStruct(final String key, final Struct struct) {
        if (locked) throw new IllegalStateException("Attempted to register a value for a Locked Struct");
        Objects.requireNonNull(key);
        if (key.contains(".")) throw new IllegalStateException("Attempted to register a field with a nested key");
        if (structValues.containsKey(key))
            throw new IllegalStateException("Attempted to re-register the Field: " + key);
        structOffsets.put(key, getSize());
        structValues.put(key, struct);
    }

    protected void registerPadding(final int length) {
        if (locked) throw new IllegalStateException("Attempted to register Padding for a Locked Struct");
        if (length <= 0) throw new IllegalArgumentException("Length must be positive");
        registerValue("Padding" + paddingFieldCount, new PaddingValue(length));
        this.paddingFieldCount += 1;
    }

    protected void registerPointer(String key, PointerType pointer, boolean loadEager) {
        if (locked) throw new IllegalStateException("Attempted to register a pointer for a Locked Struct");
        Objects.requireNonNull(key);
        if (key.contains(".")) throw new IllegalStateException("Attempted to register a field with a nested key");
        if(structValues.containsKey(key))
            throw new IllegalStateException("Attempted to re-register the Field: " + key);
        Objects.requireNonNull(pointer);

    }
    protected void registerArray(String key, Array<?> array, boolean loadEager) {
        if (locked) throw new IllegalStateException("Attempted to register a array for a Locked Struct");
        Objects.requireNonNull(key);
        if (key.contains(".")) throw new IllegalStateException("Attempted to register a field with a nested key");
        if(structValues.containsKey(key))
            throw new IllegalStateException("Attempted to re-register the Field: " + key);
        Objects.requireNonNull(array);
        if (array.isForPointers()) array.makeAllEager(loadEager);
        structValues.put(key, array);
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
        if (offset.contains(".")) return OffsetUtil.getStructOffset(this, offset);
        return structOffsets.get(offset);
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

    public static Struct createNamelessStruct(final Map<String, MemoryType> structValues) {
        final Struct baseStruct = new Struct();
        structValues.forEach((key, value) ->{
            switch(value) {
                case Array<?> array -> baseStruct.registerArray(key, array, false);
                case PointerType pointerType -> baseStruct.registerPointer(key, pointerType, false);
                case Struct struct -> baseStruct.registerStruct(key, struct);
                case Value<?> value1 -> baseStruct.registerValue(key, value1);
            }
        });
        baseStruct.lockStruct();
        return baseStruct;
    }

}
