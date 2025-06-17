package org.crain.dme4j.core.mapper;

import org.crain.dme4j.core.exception.IllegalTypeException;
import org.crain.dme4j.core.exception.InvalidStructureException;
import org.crain.dme4j.core.exception.MappingException;
import org.crain.dme4j.core.exception.MissingMappingException;
import org.crain.dme4j.core.types.*;

import java.util.Objects;

/**
 * A simple {@link MemoryTypeConverterRepository} that only supports the {@link org.crain.dme4j.core.types.Value Value} types.
 */
public class PrimitiveMemoryTypeConverterRepository implements MemoryTypeConverterRepository {

    /**
     * {@inheritDoc}
     */
    @Override
    public <Pojo, MType extends MemoryType> MemoryTypeConverter<Pojo, MType> convertTo(Pojo from, MType to) throws MappingException {
        if (from == null) throw new InvalidStructureException("Cannot convert null 'from' to a MemoryType");
        if (to == null) throw new InvalidStructureException("Cannot convert null 'to' into a MemoryType");
        if (!convertable(from, to).isConvertable()) throw new IllegalTypeException(
                "Cannot convert from " + from.getClass().getName() + " to " + to.getClass().getName()
        );

        return null; // I gotta put something in here
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <Pojo> MemoryTypeConverter<Pojo, Struct> converterFor(Class<Pojo> from) throws MissingMappingException, InvalidStructureException {
        throw new InvalidStructureException("PrimitiveMemoryTypeConverter does not support mapping to Structs!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <MType extends MemoryType> Convertable convertable(Object from, MType to) {
        return convertable(to).isConvertable() && switch (from) {
            case Byte _, Short _, Integer _, Long _, Float _, Double _, Boolean _, String _ -> true;
            default -> false;
        } ? Convertable.DIRECT : Convertable.NONCONVERTABLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <MType extends MemoryType> Convertable convertable(MType reference) {
        return reference instanceof Value ? Convertable.DIRECT : Convertable.NONCONVERTABLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryTypeConverterRepository newRepository() {
        return new PrimitiveMemoryTypeConverterRepository(); //
    }
}
