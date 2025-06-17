package org.crain.dme4j.core.exception;

/**
 * An InvalidStructureException is caused by the following,
 * <ul>
 *     <li>If deserialization was attempted, the requested pointer handling would cause an infinite loop.</li>
 *     <li>If the provided structure has any ambiguous mapping. The InvalidStructureException should wrap a more clear Exception in this case.</li>
 * </ul>
 * For example, with duplicate keys the InvalidStructException should be wrapping a {@link DuplicateException}.
 */
public final class InvalidStructureException extends MappingException {

    public InvalidStructureException(final String message) {
        super(message);
    }

    public InvalidStructureException(MappingException cause) {
        super(cause);
    }
}
