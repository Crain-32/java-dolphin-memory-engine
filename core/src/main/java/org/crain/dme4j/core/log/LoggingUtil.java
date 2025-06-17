package org.crain.dme4j.core.log;


import org.slf4j.Logger;

public class LoggingUtil {
    private LoggingUtil() { throw new IllegalStateException("Utility class"); }

    public static String ClassWithHash(Object object) {
        if (object == null) return "null@0000";
        return object.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(object));
    }

    public static String ClassBasedOnLogLevel(Object object, Logger logger) {
        if (logger.isTraceEnabled()) return ClassWithHash(object);
        if (object == null) return "null";
        return object.getClass().getName();
    }
}
