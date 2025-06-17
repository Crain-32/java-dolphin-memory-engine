package org.crain.dme4j.core.exception;

/**
 * Although not obvious at first, there are invalid states for the Engine to be in for some actions.
 * For example if you request a snapshot, but the engine isn't ready for that, it should error.
 * Another invalid state would be a mapping call with no provided TypeRegistry.
 */
public final class EngineStateException extends EngineException {
    EngineStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
