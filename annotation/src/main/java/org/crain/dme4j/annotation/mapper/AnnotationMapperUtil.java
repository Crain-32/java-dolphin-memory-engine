package org.crain.dme4j.annotation.mapper;

import org.crain.dme4j.core.mapper.MemoryTypeConverter;
import org.crain.dme4j.core.types.Struct;

class AnnotationMapperUtil {
    private AnnotationMapperUtil() {throw new IllegalCallerException("Utility class");}

    static <Pojo> MemoryTypeConverter<Pojo, Struct> structMapperFor(Class<Pojo> klazz) {

    }


}
