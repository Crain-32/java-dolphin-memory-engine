package org.crain.dme4j.core.types;

/**
 *
 * {@link StringPointer StringPointers} are a convenience class based around String.
 * They deviate from {@link ClassRefPointer} by being "Eager" by default.
 */
public final class StringPointer implements PointerType {

    private long pointerAddress;
    private String stringValue;
    private int maxLength;
    private boolean followPointer = false;
    private boolean isEager = true;

    /**
     * @param pointerAddress Address of the String, can be 0
     * @param stringValue Nullable String
     * @param maxLength Largest Possible String addressable by this Pointer
     * @throws IllegalArgumentException if {@code pointerAddress < 0 || maxLength < 1}
     */
    public StringPointer(long pointerAddress, String stringValue, final int maxLength) throws IllegalArgumentException {
        if (pointerAddress < 0) throw new IllegalArgumentException("Pointer address cannot be negative");
        if (maxLength < 1) throw new IllegalArgumentException("Max length must be positive");

        this.pointerAddress = pointerAddress;
        this.stringValue = stringValue;
        this.maxLength = maxLength;
    }

    /**
     * Delegates to {@link StringPointer#StringPointer(long, String, int)}
     * @param pointerAddress Address of the String
     * @param stringValue value of the String to wrap
     * @throws IllegalArgumentException in line with delegated Constructor
     * @throws NullPointerException if {@code stringValue} is null
     */
    public StringPointer(long pointerAddress, String stringValue) throws IllegalArgumentException, NullPointerException {
        this(pointerAddress, stringValue, stringValue.length());
    }

    /**
     * Delegates to {@link StringPointer#StringPointer(long, String, int)}, with a null String value.
     * @param pointerAddress Address of the String
     * @param maxLength Largest Possible String addressable by this Pointer
     * @throws IllegalArgumentException in line with delegated Constructor
     */
    public StringPointer(final long pointerAddress, final int maxLength) throws IllegalArgumentException {
        this(pointerAddress, null, maxLength);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPointerAddress(final long pointerAddress) {
        this.pointerAddress = pointerAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getPointerAddress() {
        return pointerAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean pointsNull() {
        return pointerAddress == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void eager(boolean eager) {
        this.isEager = eager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEager() {
        return isEager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shouldFollowPointer(boolean followPointer) {
        this.followPointer = followPointer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean willFollowPointer() {
        return isEager || followPointer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearFollow() {
        this.followPointer = false;
    }

    /**
     * @param stringValue to set
     * @throws IllegalArgumentException if the provided String is longer than {@link StringPointer#maxLength}
     */
    public void setStringValue(final String stringValue) throws IllegalArgumentException {
        if (stringValue != null && stringValue.length() > maxLength) {
            throw new IllegalArgumentException("String value cannot be longer than max length");
        }
        this.stringValue = stringValue;
    }

    /**
     * @return current String, potentially null.
     */
    public String getStringValue() {
        return this.stringValue;
    }

    /**
     * @return Max Length of Memory this buffer supports.
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * @param maxLength New max length for the buffer
     * @throws IllegalArgumentException if {@code maxLength < 1} or if it would truncate the current String.
     */
    @SuppressWarnings("unsused")
    public void setMaxLength(final int maxLength) throws IllegalArgumentException {
        if (maxLength < 1) throw new IllegalArgumentException("Max length must be positive");
        if (this.stringValue != null && this.stringValue.length() > maxLength) {
            throw new IllegalArgumentException("New max length would truncate current String");
        }
        this.maxLength = maxLength;
    }

    /**
     * @return True if this String or it's Pointer is {@code null}
     */
    public boolean isNull() {
        return stringValue == null && pointsNull();
    }

    /**
     * @return True if {@link StringPointer#isNull()} or the current value is a blank String.
     */
    public boolean isEmpty() {
        return isNull() || stringValue.isEmpty();
    }

    /**
     * Delegates to {@link StringPointer#emptyStringPointer(long, int)} with a Pointer Address of 0
     * @param maxLength Max Length of the String Buffer.
     */
    public static StringPointer nullStringPointer(final int maxLength) {
        return emptyStringPointer(0, maxLength);
    }

    /**
     * Delegates to {@link StringPointer#StringPointer(long, String, int)}, with a null String value.
     * @param pointerAddress Address of the Pointer in Memory, can be zero
     * @param maxLength Max Length of the String Buffer
     * @throws IllegalArgumentException in relationship to the Delegated Constructor
     */
    public static StringPointer emptyStringPointer(final long pointerAddress, final int maxLength) throws IllegalArgumentException {
        return new StringPointer(pointerAddress, null, maxLength);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRepresentation() {
        return "0x" + Long.toHexString(pointerAddress) + ": " + stringValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return PointerType.getPointerSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> pointsAt() {
        return String.class;
    }
}
