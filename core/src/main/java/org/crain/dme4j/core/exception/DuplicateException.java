package org.crain.dme4j.core.exception;

import org.crain.dme4j.core.mapper.MemoryTypeConverterRepository;
import org.crain.dme4j.core.mapper.MemoryTypeConverter;

/**
 * A mapping is considered a duplicate in the following situations.
 * <ul>
 *     <li>The requested namespace already exists in the {@link org.crain.dme4j.core.context.TypeRegistry TypeRegistry}</li>
 *     <li>The {@link MemoryTypeConverter} already exists in the {@link MemoryTypeConverterRepository MapperHolder}</li>
 *     <li>Declared fields in Type have name collisions.</li>
 * </ul>
 */
public final class DuplicateException extends MappingException {
    DuplicateException(String message) {
        super(message);
    }
}
