package org.crain.dme4j.core.exception;

/**
 * Used internally as the default for where CustomizableDmeException can be thrown.
 */
public final class NotDefinedException extends CustomizableDmeException {
    NotDefinedException(String message) {
        super(message);
    }
}
