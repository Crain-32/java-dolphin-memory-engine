package org.crain.dolphin.platform.windows;

public class WindowsFunctionUtil {

    /**
     * Forces the class to refind all native functions.
     */
    public static void forceReload() {
        WindowsLogger.WINDOWS_LOGGER.trace("WindowsFunctionUtil::forceReload");
        WindowsProcessMemory.linkNativeFunctions(true);
        WindowsError.linkNativeFunctions(true);
        WindowsKernel.linkNativeFunctions(true);
    }

    /**
     * Allows a cache to be used for the Native Function.
     */
    public static void setup() {
        WindowsLogger.WINDOWS_LOGGER.trace("WindowsFunctionUtil::setup");
        WindowsProcessMemory.linkNativeFunctions(false);
        WindowsError.linkNativeFunctions(false);
        WindowsKernel.linkNativeFunctions(false);
    }
}
