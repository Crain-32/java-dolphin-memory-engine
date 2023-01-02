package jna;

import org.apache.commons.lang.SystemUtils;

public class LibNameUtil {

    public static String getTargetLib() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return "window_engine";
        } else if (SystemUtils.IS_OS_LINUX) {
            return "linux_engine";
        } else if (SystemUtils.IS_OS_MAC) {
            throw new UnsupportedOperationException("Mac OS is not yet supported");
        } else {
            throw new IllegalStateException("The Operating System could not be figured out");
        }
    }

    public static String getLibType() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return ".dll";
        } else if (SystemUtils.IS_OS_LINUX) {
            return ".so";
        } else if (SystemUtils.IS_OS_MAC) {
            throw new UnsupportedOperationException("Mac OS is not yet supported");
        } else {
            throw new IllegalStateException("The Operating System could not be figured out");
        }
    }
}
