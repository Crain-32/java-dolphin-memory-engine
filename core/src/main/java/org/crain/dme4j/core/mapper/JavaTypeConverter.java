package org.crain.dme4j.core.mapper;

import org.crain.dme4j.core.types.MemoryType;

import java.util.function.Function;

/**
 * Inverse operation of the {@link MemoryTypeConverter}, mapping from native memory to Java.
 */
public interface JavaTypeConverter<MType extends MemoryType, Pojo> extends Function<MType, Pojo> {
}
