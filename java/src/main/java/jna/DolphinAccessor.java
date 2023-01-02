package jna;

import com.sun.jna.NativeLong;
import com.sun.jna.win32.StdCallLibrary;

public interface DolphinAccessor extends StdCallLibrary {

    void init();

    void hook();

    Boolean getStatus();

    Long getMemOne();

    void readFromRAM(NativeLong consoleAddress, NativeLong size, byte[] buffer);

    Boolean writeToRAM(NativeLong consoleAddress, NativeLong size, byte[] buffer);
}
