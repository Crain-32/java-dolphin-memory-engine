package org.crain.dme4j.core.exception;

/**
 * Access point for non-core exceptions. Default handler is a No-op that either converts into an IllegalTypeException
 * if handling is required, otherwise it will discard the Exception.
 */
public non-sealed class CustomizableDmeException extends DmeCoreModuleException {
    protected CustomizableDmeException(String message) {
        super(message);
    }
}
