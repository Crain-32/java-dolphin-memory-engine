package org.crain.dme4j.core.exception;

import org.crain.dme4j.core.mapper.MemoryTypeConverter;

/**
 * MissingMappingException covers the following situations.
 * <ul>
 *     <li>Attempted Registration in the {@link org.crain.dme4j.core.context.TypeRegistry TypeRegistry} failed to find a required Struct</li>
 *     <li>Attempted deserialization failed to find a {@link MemoryTypeConverter MemoryTypeMapper} for the Type</li>
 *     <li></li>
 * </ul>
 */
public final class MissingMappingException extends MappingException {

    // Need a reference that isn't going to ever come from a consumer
    private static class NotProvided {
        private NotProvided() { throw new UnsupportedOperationException(); }
    }

    private Class<?> missingKlazz = NotProvided.class;

    MissingMappingException(String message) {
        super(message);
    }

    MissingMappingException(String message, Class<?> missingKlazz) {
        super(message);
        this.missingKlazz = missingKlazz;
    }
}
