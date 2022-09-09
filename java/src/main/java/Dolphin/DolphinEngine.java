package Dolphin;


import com.sun.jna.Native;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.win32.W32APIOptions;
import jna.DolphinAccessor;
import jna.LibNameUtil;

import java.nio.ByteBuffer;

public class DolphinEngine {

    private static DolphinAccessor dolphinAccessor;


    public DolphinEngine() {
        if (dolphinAccessor == null) {
            dolphinAccessor = dolphinAccessorFactory();
            dolphinAccessor.init();
        }
    }

    private DolphinAccessor dolphinAccessorFactory() {
        return Native.load(LibNameUtil.getTargetLib(), DolphinAccessor.class, W32APIOptions.DEFAULT_OPTIONS);
    }

    public Boolean hook() {
        dolphinAccessor.hook();
        return getStatus() == DolphinStatus.HOOKED;
    }

    public Boolean unhook() {
        dolphinAccessor.unhook();
        return getStatus() != DolphinStatus.HOOKED;
    }

    public DolphinStatus getStatus() {
        return DolphinStatus.fromVal(dolphinAccessor.getStatus());
    }

    public Boolean dolphinRunning() {
        return dolphinAccessor.getPID() != 0;
    }

    public Byte readByteFromRAM(Integer consoleAddress) {
        return (Byte) readFromRAM(consoleAddress, 1);
    }

    public Boolean writeByteToRAM(Integer consoleAddress, Byte val) {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put(val);
        return writeToRAM(consoleAddress, 1, buffer.array());
    }

    public Short readShortFromRAM(Integer consoleAddress) {
        return (Short) readFromRAM(consoleAddress, 2);
    }

    public Boolean writeShortToRAM(Integer consoleAddress, Short val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(val);
        return writeToRAM(consoleAddress, 2, buffer.array());
    }

    public Integer readIntegerFromRAM(Integer consoleAddress) {
        return (Integer) readFromRAM(consoleAddress, 4);
    }

    public Boolean writeIntegerToRAM(Integer consoleAddress, Integer val) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(val);
        return writeToRAM(consoleAddress, 4, buffer.array());
    }

    public Long readLongFromRAM(Integer consoleAddress) {
        return (Long) readFromRAM(consoleAddress, 8);
    }

    public Boolean writeLongToRAM(Integer consoleAddress, Long val) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(val);
        return writeToRAM(consoleAddress, 8, buffer.array());
    }

    public Float readFloatFromRAM(Integer consoleAddress) {
        return Float.intBitsToFloat(readIntegerFromRAM(consoleAddress));
    }

    public Boolean writeFloatToRAM(Integer consoleAddress, Float val) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putFloat(val);
        return writeToRAM(consoleAddress, 4, buffer.array());
    }

    public Double readDoubleFromRAM(Integer consoleAddress) {
        return Double.longBitsToDouble(readLongFromRAM(consoleAddress));
    }

    public Boolean writeDoubleToRAM(Integer consoleAddress, Double val) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putDouble(val);
        return writeToRAM(consoleAddress, 8, buffer.array());
    }

    public String readStringFromRAM(Integer consoleAddress, Integer size) {
        byte[] buffer = new byte[size];
        if (!dolphinAccessor.readFromRAM(consoleAddress, buffer, new BaseTSD.SIZE_T(size), false)) {
            return "";
        }
        return Native.toString(buffer);
    }

    public Boolean writeStringToRAM(Integer consoleAddress, String val) {
        byte[] buffer = Native.toByteArray(val);
        return writeToRAM(consoleAddress, buffer.length, buffer);
    }

    private Number readFromRAM(Integer consoleAddress, Integer size) {
        byte[] buffer = new byte[size];
        if (!dolphinAccessor.readFromRAM(consoleAddress, buffer, new BaseTSD.SIZE_T(size), false)) {
            return 0;
        }
        long result = 0L;
        for (int index = 0; index < buffer.length; index++) {
            result = result << (8 * index);
            result |= buffer[index] ;
        }
        return switch (size) {
            case 1 -> (byte) result;
            case 2 -> (short) result;
            case 4 -> (int) result;
            case 8 -> result;
            default -> throw new IllegalArgumentException("Size of " + size + " is not supported!");
        };
    }

    private Boolean writeToRAM(Integer consoleAddress, Integer size, byte[] val) {
        if (dolphinAccessor == null) {
            return false;
        }
        return dolphinAccessor.writeToRAM(consoleAddress, val, new BaseTSD.SIZE_T(size), false);
    }
}
