package org.crain.dme4j.core.exception;
/**
 * For String based queries, this Exception will be thrown for invalid syntax.
 * Note that there is no syntax currently set, as there are no queries.
 *
 * For that reason this Exception is Package-Private, as there is no reason to expose it yet.
 */
final class SyntaxException extends QueryException {
    private SyntaxException(String message) {
        super(message);
    }
}
