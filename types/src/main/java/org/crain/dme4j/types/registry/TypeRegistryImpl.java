package org.crain.dme4j.types.registry;

import org.crain.dme4j.core.context.TypeRegistry;
import org.crain.dme4j.core.exception.InvalidStructureException;
import org.crain.dme4j.core.exception.MappingException;
import org.crain.dme4j.core.types.*;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TypeRegistryImpl implements TypeRegistry {

    private static final Map<String, Class<?>> aliases = new ConcurrentHashMap<>();
    private static final Map<String, Class<?>> projections = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Struct> representations = new ConcurrentHashMap<>();
//    private static final Map<Class<?>> mappers = new ConcurrentHashMap<>();

    public void registerAll(Collection<Class<?>> klazzes) throws MappingException {
        for (Class<?> klazz : klazzes) {
            this.registerType(klazz);
        }
    }

    public void registerType(Class<?> klazz) throws MappingException {
    }

    public void registerProjection(Class<?> klazz) throws MappingException {
        if (!ProjectionUtil.canProject(klazz)) {
            throw new InvalidStructureException("Provided projection isn't a simple interface");
        }
    }

    @Override
    public boolean containsRegistration(Class<?> klazz) {
        return false;
    }
}
