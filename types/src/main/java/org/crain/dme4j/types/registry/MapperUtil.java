package org.crain.dme4j.types.registry;

import org.crain.dme4j.core.mapper.MemoryTypeConverter;
import org.crain.dme4j.core.types.*;

class MapperUtil {
    private MapperUtil() {
        throw new IllegalStateException("Utility class");
    }
    record MapperPair<T>(Class<T> klazz, Struct representation, MemoryTypeConverter<T, Struct> mapper) {
    }

    static <T> MapperPair<T> pairFor(Class<T> klazz) {
        return null;
    }

    static <T> MemoryTypeConverter<T, Struct> structMapperFor(Class<T> klazz) {
        return null;
    }
}
