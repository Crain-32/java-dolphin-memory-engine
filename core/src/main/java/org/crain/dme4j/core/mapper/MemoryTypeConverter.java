package org.crain.dme4j.core.mapper;

import org.crain.dme4j.core.types.MemoryType;

import java.util.function.Function;

/**
 * Maps from Java to native memory.
 */
public interface MemoryTypeConverter<Pojo, MType extends MemoryType> extends Function<Pojo, MType> {
}
