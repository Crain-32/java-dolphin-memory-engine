package org.crain.dme4j.core.exception;

import org.crain.dme4j.core.mapper.MemoryTypeConverter;

/**
 * An IllegalTypeException is sourced from two main points.
 * <ul>
 *     <li>From the {@link org.crain.dme4j.core.context.TypeRegistry TypeRegistry} implementation.</li>
 *     <li>From attempted registration of a {@link MemoryTypeConverter MemoryTypeMapper}</li>
 * </ul>
 */
public final class IllegalTypeException extends MappingException {
    public IllegalTypeException(String message) {
        super(message);
    }
}
