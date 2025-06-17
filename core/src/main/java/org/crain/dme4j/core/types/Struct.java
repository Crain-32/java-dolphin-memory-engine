package org.crain.dme4j.core.types;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public non-sealed class Struct implements MemoryType {

    private static final AtomicInteger UnnamedStructsId = new AtomicInteger();

    private final SequencedMap<String, MemoryType> structValues;
    private final SequencedMap<String, Integer> structOffsets;
    private final int unsetId;
    private boolean locked = false;
    private boolean containsPointer = false;
    private int finalSize = 0;
    private int paddingFieldCount = 0;

    protected Struct() {
        structValues = new LinkedHashMap<>();
        structOffsets = new LinkedHashMap<>();
        unsetId = UnnamedStructsId.getAndIncrement();
    }

    // These are mostly the same... need to isolate which ones actually need to be separate.
    protected void registerValue(final String key, final Value<?> value) {
        String errorMessage = validateStructAndKey(key);
        if (errorMessage != null) {
            throw new IllegalArgumentException(errorMessage.formatted("Value"));
        }
        Objects.requireNonNull(value);
        structOffsets.put(key, getSize());
        structValues.put(key, value);
    }

    protected void registerStruct(final String key, final Struct struct) {
        String errorMessage = validateStructAndKey(key);
        if (errorMessage != null) {
            throw new IllegalArgumentException(errorMessage.formatted("Struct"));
        }
        structOffsets.put(key, getSize());
        structValues.put(key, struct);
    }

    protected void registerPadding(final int length) {
        if (locked) throw new IllegalStateException("Attempted to register Padding for a Locked Struct");
        if (length <= 0) throw new IllegalArgumentException("Length must be positive");
        registerValue("Padding" + paddingFieldCount, PaddingValue.of(length));
        this.paddingFieldCount += 1;
    }

    protected void registerPointer(String key, PointerType pointer, boolean loadEager) {
        String errorMessage = validateStructAndKey(key);
        if (errorMessage != null) {
            throw new IllegalArgumentException(errorMessage.formatted("Pointer"));
        }
        Objects.requireNonNull(pointer);
        pointer.eager(loadEager);
        containsPointer = true;
        structOffsets.put(key, getSize());
        structValues.put(key, pointer);
    }

    /**
     * Registers the {@link Array} in the Struct's definition
     * @param key Reference key for this Array Field
     * @param array Array definition to map to this.
     * @param loadEager If the array should be eagerly loaded or not.
     */
    protected void registerArray(String key, Array<?> array, @SuppressWarnings("SameParameterValue") boolean loadEager) {
        String errorMessage = validateStructAndKey(key);
        if (errorMessage != null) {
            throw new IllegalArgumentException(errorMessage.formatted("Array"));
        }
        Objects.requireNonNull(array);
        if (array.isForPointers()) array.makeAllEager(loadEager);
        structOffsets.put(key, getSize());
        structValues.put(key, array);
    }

    /**
     * Validates that the provided Key String follows these properties.
     * <ul>
     *     <li>Nonnull</li>
     *     <li>Not Empty</li>
     *     <li>Not describing a nested relationship (has a period)</li>
     * </ul>
     * It also checks if the Struct is locked, and by extension cannot accept new Keys.
     * @param key The String to check against.
     * @return A reasonable error message that should be formatted with "Value" unless you know more.
     */
    protected String validateStructAndKey(String key) {
        if (locked) return "Attempted to register a %s for a Locked Struct";
        if (Objects.isNull(key) || key.isBlank()) return "Attempted to register an empty key associated to a %s";
        if (key.contains(".")) return "Attempted to register a field with a nested key for type %s";
        if (structValues.containsKey(key)) return "Attempted to re-register the Field " + key + " with the type %s";
        return null;
    }

    public String getName() {
        return "UnnamedStruct#" + unsetId;
    }

    /**
     * Locks the Struct, preventing further modification of the Structure.
     * Keep in mind that this does not prevent the modification of the values.
     */
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

    /**
     * Similar to getUncheckedValue, however it does do an actual Type check, so the value must be nonnull and of Type T
     * @param key Key for the Struct to check against.
     * @param type Class Type to check against, the value for Key must be hand
     * @return Optional of Type T if the value exists, otherwise an Empty Optional
     * @param <T> Memory Type the value must pass an isInstance of check against.
     */
    public <T extends MemoryType> Optional<T> getCheckedValue(String key, Class<T> type) {
        Objects.requireNonNull(type, "Provided Class reference cannot be null");
        T value = getUncheckedValue(key);
        if (type.isInstance(value)) {
            return Optional.of(type.cast(value));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns a potentially null value associated to the Key. No additional Type Checking is done.
     * @param key Key of the Struct to get.
     * @return A potentially null Object associated at the key.
     */
    public MemoryType checkYourselfValue(String key) {
        return structValues.getOrDefault(key, null);
    }

    /**
     * @return The Fields of the Struct in the same order they were declared. This set cannot be modified.
     */
    public SequencedSet<String> structFieldNames() {
        return Collections.unmodifiableSequencedSet(structValues.sequencedKeySet());
    }

    /**
     * The offset of a Struct is a required part of Serialization. As such we do this.
     * @param offset Either a nested key String, or a Key, which
     * @return
     */
    public int structOffset(String offset) {
        return (offset.contains(".")) ? OffsetUtil.getStructOffset(this, offset) : structOffsets.get(offset);
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
        structValues.forEach((key, value) -> {
            switch (value) {
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
