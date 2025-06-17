package org.crain.dme4j.core.mapper;

import org.crain.dme4j.core.exception.MappingException;
import org.crain.dme4j.core.types.MemoryType;

/**
 * This mirrors the {@link MemoryTypeConverterRepository} in functionality, just for {@link JavaTypeConverter} instances.
 * {@link MemoryTypeConverterRepository} for more information.
 */
public interface JavaTypeConverterRepository {

    /**
     * @param to      Nonnull reference to a {@link MemoryType} that will be mapped from. {@link org.crain.dme4j.core.types.Array Array} will throw a {@link org.crain.dme4j.core.exception.IllegalTypeException IllegalTypeException}
     * @param from    Nonnull reference to the source Class or Interface that will be created from the requested {@link MemoryType}
     * @param <MType> The {@link MemoryType} that will be mapped from.
     * @param <Pojo>  A consumer defined class, likely in the {@link org.crain.dme4j.core.context.TypeRegistry TypeRegistry}.
     * @return A {@link JavaTypeConverter} that was potentially just created.
     * @throws MappingException If the requested {@link MemoryType} is {@link org.crain.dme4j.core.types.Array Array}, or if the {@link JavaTypeConverter} cannot be found or created.
     */
    <MType extends MemoryType, Pojo> JavaTypeConverter<MType, Pojo> converterFor(Class<MType> to, Class<Pojo> from)
            throws MappingException;


    /**
     * Although this can check for if {@link JavaTypeConverterRepository#converterFor(Class, Class)} will find a mapper. It will not
     * check if the {@link JavaTypeConverter} will be dynamically created.
     */
    <MType extends MemoryType> Convertable convertable(Class<MType> from, Class<?> to);
}
