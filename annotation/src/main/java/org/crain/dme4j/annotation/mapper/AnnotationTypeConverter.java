package org.crain.dme4j.annotation.mapper;

import org.crain.dme4j.core.exception.InvalidStructureException;
import org.crain.dme4j.core.exception.MappingException;
import org.crain.dme4j.core.exception.MissingMappingException;
import org.crain.dme4j.core.mapper.Convertable;
import org.crain.dme4j.core.mapper.MemoryTypeConverterRepository;
import org.crain.dme4j.core.mapper.MemoryTypeConverter;
import org.crain.dme4j.core.mapper.PrimitiveMemoryTypeConverterRepository;
import org.crain.dme4j.core.types.*;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the {@link MemoryTypeConverterRepository} that handles mapping annotation driven classes to MemoryType instances
 */
public class AnnotationTypeConverter implements MemoryTypeConverterRepository {

    private final Map<?, ? extends MemoryType> mappers = new ConcurrentHashMap<>();
    private MemoryTypeConverterRepository simpleRepository;

    private AnnotationTypeConverter() {
        this.simpleRepository = ServiceLoader.load(MemoryTypeConverterRepository.class).stream()
                .filter(load -> PrimitiveMemoryTypeConverterRepository.class.equals(load.type()))
                .findFirst()
                .map(ServiceLoader.Provider::get)
                .orElse(null);
    }

    private static final class InstanceHolder {
        private static final AnnotationTypeConverter INSTANCE = new AnnotationTypeConverter();
    }

    public static MemoryTypeConverterRepository provider() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public <Pojo, MType extends MemoryType> MemoryTypeConverter<Pojo, MType> convertTo(Pojo from, MType to) throws MappingException {
        if (!Struct.class.isAssignableFrom(to.getClass())) {
            throw new InvalidStructureException("This Mapper can only support mapping to Structs");
        }
        return (MemoryTypeConverter<Pojo, MType>) converterFor(from.getClass());
    }

    @Override
    public <Pojo> MemoryTypeConverter<Pojo, Struct> converterFor(Class<Pojo> from) throws MissingMappingException, InvalidStructureException {
        if (Array.class.isAssignableFrom(from)) {
            throw new InvalidStructureException("Arrays cannot be mapped to Structs");
        } else if (PointerType.class.isAssignableFrom(from)) {
            throw new InvalidStructureException("PointerTypes are inherently supported");
        }
        // Now the sucky part
        return null;
    }

    @Override
    public <MType extends MemoryType> Convertable convertable(Object from, MType to) {
        var resultType = mappers.get(from);
//        return resultType != null && resultType.getClass().equals(to.getClass());
        return Convertable.NONCONVERTABLE;
    }

    @Override
    public <MType extends MemoryType> Convertable convertable(MType instance) {
//        return this.simpleRepository != null;
        return Convertable.NONCONVERTABLE;
    }

    @Override
    public MemoryTypeConverterRepository newRepository() {
        return new AnnotationTypeConverter();
    }
}
