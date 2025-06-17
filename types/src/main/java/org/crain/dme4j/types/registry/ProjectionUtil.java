package org.crain.dme4j.types.registry;

class ProjectionUtil {
    private ProjectionUtil() {
        throw new IllegalStateException("Utility class");
    }

    static boolean canProject(Class<?> klazz) {
        return klazz.isInterface() &&  klazz.getAnnotatedInterfaces().length == 0;
    }
}
