package org.crain.dme4j.engine.dolphin.platform.windows;

import org.crain.dme4j.engine.dolphin.platform.util.NativeHelper;

import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.JAVA_INT;

public class WindowsError {

    private static MethodHandle getLastErrorMethod;

    /**
     * Note, this can easily fail to get the right error since the JVM thread running this application
     * could cause a new error, and Windows is override us.
     */
    public static int GetLastError() {
        try {
            return (int) getLastErrorMethod.invoke();
        } catch (Throwable e) {
            WindowsLogger.WINDOWS_LOGGER.atError()
                    .setCause(e)
                    .log("GetLastError Failed");
            return -1;
        }
    }

    static void linkNativeFunctions(final boolean forceFind) {
        WindowsLogger.WINDOWS_LOGGER.atTrace()
                .setMessage("WindowsError::linkNativeFunctions({})")
                .addArgument(forceFind)
                .log();
        synchronized (WindowsError.class) {
            if (getLastErrorMethod == null || forceFind) {
                getLastErrorMethod = NativeHelper.findNativeMethod(
                        "Kernel32.dll",
                        "GetLastError",
                        JAVA_INT
                );
            }
        }
    }
}
