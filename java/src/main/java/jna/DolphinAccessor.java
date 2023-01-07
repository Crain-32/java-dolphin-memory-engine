package jna;

import com.sun.jna.Library;
import com.sun.jna.NativeLong;

public interface DolphinAccessor {

    void init();

    void hook();

    Boolean getStatus();

    Long getMemOne();

    Integer find_pid();

    Boolean check_string(byte[] java_str);

    Boolean check_pid_from_str(byte[] java_str, byte[] buffer);

    Long check_ram_info();

    void readFromRAM(NativeLong consoleAddress, NativeLong size, byte[] buffer);

    Boolean writeToRAM(NativeLong consoleAddress, NativeLong size, byte[] buffer);
}
