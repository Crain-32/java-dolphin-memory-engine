package org.crain.dme4j.core.exception;

/**
 * The {@link EngineException} handles Exceptions specifically targeted at the Engine's connection, or the Engine's
 * state.
 * <br>
 * For example if you attempt a Write action with an Engine that only supports Read actions, the expectation is that
 * a {@link EngineStateException} would be thrown. You can find a more concrete contract defined on the
 * {@link org.crain.dme4j.core.GamecubeMemoryEngine GamecubeMemoryEngine} interface itself.
 */
public sealed abstract class EngineException extends DmeCoreModuleException
        permits EngineConnectionException, EngineStateException {
    EngineException(String message) {
        super(message);
    }
    EngineException(String message, Throwable cause) {
        super(message, cause);
    }
}
