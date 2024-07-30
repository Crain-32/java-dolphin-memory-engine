package org.crain.dolphin;

import org.apache.commons.lang3.SystemUtils;
import org.crain.dolphin.platform.windows.WindowsFunctionUtil;
import org.crain.memory.GamecubeMemoryEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;

public class DolphinFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DolphinFactory.class);

    private static final String OS_FAILURE_STRING = "The current Operating System is not supported";

    private static volatile WeakReference<GamecubeMemoryEngine> dolphinEngineCache;

    public static GamecubeMemoryEngine getDolphinEngine() throws IllegalStateException {
        return getDolphinEngine(false);
    }

    public static GamecubeMemoryEngine getCachedEngine() throws IllegalStateException {
        return getDolphinEngine(true);
    }

    public static GamecubeMemoryEngine getDolphinEngine(final boolean checkDolphinCache) {
        LOGGER.atTrace()
                .setMessage("DolphinFactory::getDolphinEngine({})")
                .addArgument(checkDolphinCache)
                .log();
        if (checkDolphinCache && dolphinEngineCache.get() != null) {
            LOGGER.trace("WeakReference cache returned");
            return dolphinEngineCache.get();
        }
        OS os;
        if (SystemUtils.IS_OS_WINDOWS) {
            os = OS.WINDOWS;
        } else if (SystemUtils.IS_OS_LINUX) {
            os = OS.LINUX;
        } else if (SystemUtils.IS_OS_MAC) {
            os = OS.MAC;
        } else {
            throw new IllegalStateException(OS_FAILURE_STRING);
        }
        setUpNativeFunctions(os);
        var memoryEngine = switch (os) {
            case WINDOWS -> new WindowsDolphinEngine();
            case MAC, LINUX -> throw new IllegalStateException(OS_FAILURE_STRING);
        };
        dolphinEngineCache = new WeakReference<>(memoryEngine);
        return memoryEngine;
    }

    private enum OS {
        WINDOWS,
        MAC,
        LINUX
    }

    private static void setUpNativeFunctions(OS os) {
        LOGGER.atTrace()
                .setMessage("DolphinFactory::setUpNativeFunctions({})")
                .addArgument(os)
                .log();
        switch (os) {
            case WINDOWS -> WindowsFunctionUtil.setup();
            case MAC, LINUX -> throw new IllegalStateException(OS_FAILURE_STRING);
        }
    }
}
