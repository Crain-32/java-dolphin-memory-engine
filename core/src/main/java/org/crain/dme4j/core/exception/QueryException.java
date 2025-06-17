package org.crain.dme4j.core.exception;

/**
 * QueryExceptions are typically going to be wrapping a more specific exception.
 *
 * For example an {@link ExecutionException} might wrap a {@link EngineConnectionException}.
 */
public abstract sealed class QueryException extends DmeCoreModuleException
        permits SyntaxException, ExecutionException {
    QueryException(String message) {
        super(message);
    }
}
