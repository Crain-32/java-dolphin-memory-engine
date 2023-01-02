package dolphin;


import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.win32.W32APIOptions;
import jna.DolphinAccessor;
import jna.LibNameUtil;
import org.apache.commons.lang.SystemUtils;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class DolphinEngine {

    private DolphinAccessor dolphinAccessor;

    public DolphinEngine() {
        if (dolphinAccessor == null) {
            dolphinAccessor = dolphinAccessorFactory();
            dolphinAccessor.init();
        }
    }

    private DolphinAccessor dolphinAccessorFactory() {
        Map<String, Object> options = new HashMap<>();
        if (SystemUtils.IS_OS_WINDOWS) {
            options.putAll(W32APIOptions.DEFAULT_OPTIONS);
        }
        return Native.load(LibNameUtil.getTargetLib(), DolphinAccessor.class, options);
    }

    public Boolean hook() {
        dolphinAccessor.hook();
        return true;
    }

    public Long getPId() {
        return dolphinAccessor.getMemOne();
    }

    public Boolean getStatus() {
        return dolphinAccessor.getStatus();
    }

    public Boolean dolphinRunning() {
        return dolphinAccessor.getMemOne() != 0;
    }

    public Byte readByteFromRAM(Long consoleAddress) {
        return (Byte) readFromRAM(consoleAddress, 1);
    }

    public Boolean writeByteToRAM(Long consoleAddress, Byte val) {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put(val);
        return writeToRAM(consoleAddress, buffer.array());
    }

    public Short readShortFromRAM(Long consoleAddress) {
        return (Short) readFromRAM(consoleAddress, 2);
    }

    public Boolean writeShortToRAM(Long consoleAddress, Short val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(val);
        return writeToRAM(consoleAddress, buffer.array());
    }

    public Integer readIntegerFromRAM(Long consoleAddress) {
        try {
            return (Integer) readFromRAM(consoleAddress, 4);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Boolean writeIntegerToRAM(Long consoleAddress, Integer val) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(val);
        return writeToRAM(consoleAddress, buffer.array());
    }

    public Long readLongFromRAM(Long consoleAddress) {
        return (Long) readFromRAM(consoleAddress, 8);
    }

    public Boolean writeLongToRAM(Long consoleAddress, Long val) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(val);
        return writeToRAM(consoleAddress, buffer.array());
    }

    public Float readFloatFromRAM(Long consoleAddress) {
        return Float.intBitsToFloat(readIntegerFromRAM(consoleAddress));
    }

    public Boolean writeFloatToRAM(Long consoleAddress, Float val) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putFloat(val);
        return writeToRAM(consoleAddress, buffer.array());
    }

    public Double readDoubleFromRAM(Long consoleAddress) {
        return Double.longBitsToDouble(readLongFromRAM(consoleAddress));
    }

    public Boolean writeDoubleToRAM(Long consoleAddress, Double val) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putDouble(val);
        return writeToRAM(consoleAddress, buffer.array());
    }

    public String readStringFromRAM(Long consoleAddress, Integer size) {
        ByteBuffer buffer = ByteBuffer.allocate(size);
        dolphinAccessor.readFromRAM(new NativeLong(consoleAddress), new NativeLong(size), buffer.array());
        return buffer.array().length == 0 ? "" : Native.toString(buffer.array());
    }

    public Boolean writeStringToRAM(Long consoleAddress, String val) {
        byte[] buffer = Native.toByteArray(val);
        return writeToRAM(consoleAddress, buffer);
    }

    private Number readFromRAM(Long consoleAddress, Integer size) {
        byte[] buffer = new byte[size + 1];
        dolphinAccessor.readFromRAM(new NativeLong(consoleAddress), new NativeLong(size), buffer);
        if (buffer.length == 0) {
            return 0;
        }
        long result = 0L;
        for (int index = 0; index < buffer.length; index++) {
            result = result << (8 * index);
            result |= buffer[index];
        }
        return switch (size) {
            case 1 -> (byte) result;
            case 2 -> (short) result;
            case 4 -> (int) result;
            case 8 -> result;
            default -> throw new IllegalArgumentException("Size of " + size + " is not supported!");
        };
    }

    private Boolean writeToRAM(Long consoleAddress, byte[] val) {
        if (dolphinAccessor == null) {
            return false;
        }
        return dolphinAccessor.writeToRAM(new NativeLong(consoleAddress), new NativeLong(val.length), val);
    }
}
