package jna;

import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.win32.StdCallLibrary;

public interface DolphinAccessor extends StdCallLibrary {

    void init();

    void hook();

    void unhook();

    Integer getStatus();

    Integer getPID();

    Boolean readFromRAM(int consoleAddress, byte[] buffer, BaseTSD.SIZE_T size, boolean withBSwap);

    Boolean writeToRAM(int consoleAddress, byte[] buffer, BaseTSD.SIZE_T size, boolean withDSwap);
}
