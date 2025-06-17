import org.crain.dme4j.core.mapper.MemoryTypeConverterRepository;
import org.crain.dme4j.core.mapper.PrimitiveMemoryTypeConverterRepository;

module core {
    exports org.crain.dme4j.core;
    exports org.crain.dme4j.core.types;
    exports org.crain.dme4j.core.context;
    exports org.crain.dme4j.core.mapper;
    exports org.crain.dme4j.core.exception;

    requires org.slf4j;
    requires java.logging;

    provides MemoryTypeConverterRepository with PrimitiveMemoryTypeConverterRepository;
}