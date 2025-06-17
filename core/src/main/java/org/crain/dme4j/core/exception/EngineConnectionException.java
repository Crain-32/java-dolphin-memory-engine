package org.crain.dme4j.core.exception;

/**
 * Covers any issue resulting from the Engine's Connection itself.
 */
public final class EngineConnectionException extends EngineException {
    public enum FailureType {
        NOT_FOUND,
        DISCONNECTED,
        NOT_PROVIDED
    }

    private final FailureType failureType;

    public EngineConnectionException(String message) {
        this(message, FailureType.NOT_FOUND);
    }

    public EngineConnectionException(String message, FailureType failureType) {
        super(message);
        this.failureType = failureType;
    }
    public EngineConnectionException(String message, FailureType failureType, Throwable cause) {
        super(message, cause);
        this.failureType = failureType;
    }
}
