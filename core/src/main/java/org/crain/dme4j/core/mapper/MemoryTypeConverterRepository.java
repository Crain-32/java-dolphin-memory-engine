package org.crain.dme4j.core.mapper;

import org.crain.dme4j.core.exception.InvalidStructureException;
import org.crain.dme4j.core.exception.MappingException;
import org.crain.dme4j.core.exception.MissingMappingException;
import org.crain.dme4j.core.types.MemoryType;
import org.crain.dme4j.core.types.Struct;

/**
 * The {@link MemoryTypeConverterRepository} is a simple container for {@link MemoryTypeConverter MemoryTypeMappers}, and may or may not dynamically
 * create a {@link MemoryTypeConverter} on request. Implementations must decide how the user is to provide the mappings.
 */
public interface MemoryTypeConverterRepository {

    /**
     * @param from Nonnull reference to the source Class or Interface that will map into the requested {@link MemoryType}
     * @param to Nonnull reference to a {@link MemoryType} that will be mapped into. {@link org.crain.dme4j.core.types.Array Array} will throw a {@link org.crain.dme4j.core.exception.IllegalTypeException IllegalTypeException}
     * @return A {@link MemoryTypeConverter} that was potentially just created.
     * @param <Pojo> A consumer defined class, likely in the {@link org.crain.dme4j.core.context.TypeRegistry TypeRegistry}.
     * @param <MType> The resulting {@link MemoryType}.
     * @throws MappingException If the requested {@link MemoryType} is {@link org.crain.dme4j.core.types.Array Array}, or if the {@link MemoryTypeConverter} cannot be found or created.
     */
    <Pojo, MType extends MemoryType> MemoryTypeConverter<Pojo, MType> convertTo(Pojo from, MType to)
            throws MappingException;


    /**
     * Convenience method, mirrors {@link MemoryTypeConverterRepository#convertTo(Object, MemoryType)},
     * but defines {@link org.crain.dme4j.core.types.Struct Struct} as the target.
     * @return MemoryTypeMapper that can map the Java Object to a {@link Struct}
     * @throws MissingMappingException if the mapper is not in the context, or can't be created.
     * @throws InvalidStructureException if the {@code from} field cannot be mapped. For example passing in an Array.
     */
    <Pojo> MemoryTypeConverter<Pojo, Struct> converterFor(Class<Pojo> from) throws MissingMappingException, InvalidStructureException;

    /**
     * Although this can check for if {@link MemoryTypeConverterRepository#convertTo(Object, MemoryType)} will find a mapper. It will not
     * check if the {@link MemoryTypeConverter} will be dynamically created.
     */
    <MType extends MemoryType> Convertable convertable(Object from, MType to);

    /**
     * True or false if the Mapper can support the {@code MemoryType} requested.
     */
    <MType extends MemoryType> Convertable convertable(MType instance);

    /**
     * Creates a new {@link MemoryTypeConverterRepository} of the same type, but without the underlying mappers.
     * Useful for having multiple contexts.
     */
    MemoryTypeConverterRepository newRepository();
}
