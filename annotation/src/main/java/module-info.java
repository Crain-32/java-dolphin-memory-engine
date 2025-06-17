import org.crain.dme4j.annotation.mapper.AnnotationTypeConverter;
import org.crain.dme4j.core.mapper.MemoryTypeConverterRepository;
import org.crain.dme4j.core.mapper.PrimitiveMemoryTypeConverterRepository;

module annotation {
    exports org.crain.dme4j.annotation;

    requires core;

    provides MemoryTypeConverterRepository with AnnotationTypeConverter;
    uses MemoryTypeConverterRepository;

    // Explicitly include the required implementation we need to make the AnnotationMapper work.
    uses PrimitiveMemoryTypeConverterRepository;

}