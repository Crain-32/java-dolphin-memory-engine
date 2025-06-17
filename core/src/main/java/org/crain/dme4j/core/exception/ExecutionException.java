package org.crain.dme4j.core.exception;

/**
 * Exception caused by the execution of the query.
 * This could be something like a query into unaddressable memory space, or an invalid type cast.
 */
public final class ExecutionException extends QueryException {
    public ExecutionException(String message) {
        super(message);
    }
}
