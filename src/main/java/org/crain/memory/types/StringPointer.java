package org.crain.memory.types;

/**
 * Since Strings are a Special type of Pointer, they get a special handling for now.
 */
public final class StringPointer implements PointerType {

    private long pointerAddress;
    private String stringValue = null;
    private int maxLength;
    private boolean followPointer = false;
    private boolean isEager = true;

    public StringPointer(long pointerAddress, String stringValue, final int maxLength) {
        this.pointerAddress = pointerAddress;
        this.stringValue = stringValue;
        this.maxLength = maxLength;
    }

    public StringPointer(long pointerAddress, String stringValue) {
        this(pointerAddress, stringValue, stringValue.length());
    }

    public StringPointer(final long pointerAddress, final int maxLength) {
        this(pointerAddress, null, maxLength);
    }

    @Override
    public void setPointerAddress(final long pointerAddress) {
        this.pointerAddress = pointerAddress;
    }

    @Override
    public long getPointerAddress() {
        return pointerAddress;
    }

    @Override
    public boolean pointsNull() {
        return pointerAddress == 0;
    }
    @Override
    public void eager(boolean eager) {
        this.isEager = eager;
    }
    @Override
    public boolean isEager() {
        return isEager;
    }

    @Override
    public void shouldFollowPointer(boolean followPointer) {
        this.followPointer = followPointer;
    }

    @Override
    public boolean willFollowPointer() {
        return isEager || followPointer;
    }
    @Override
    public void clearFollow() {
        this.followPointer = false;
    }

    public void setStringValue(final String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return this.stringValue;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(final int maxLength) {
        this.maxLength = maxLength;
    }

    public boolean isNull() {
        return stringValue == null && pointsNull();
    }

    public boolean isEmpty() {
        return isNull() || stringValue.isEmpty();
    }

    public static StringPointer nullStringPointer(final int maxLength) {
        return emptyStringPointer(0, maxLength);
    }

    public static StringPointer emptyStringPointer(final long pointerValue, final int maxLength) {
        return new StringPointer(pointerValue, null, maxLength);
    }

    @Override
    public String getRepresentation() {
        return "0x" + Long.toHexString(pointerAddress) + ": " + stringValue;
    }

    @Override
    public int getSize() {
        return 4;
    }
}
