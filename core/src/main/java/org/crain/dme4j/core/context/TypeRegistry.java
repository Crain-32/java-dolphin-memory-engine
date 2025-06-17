package org.crain.dme4j.core.context;

import org.crain.dme4j.core.exception.MappingException;

import java.util.Collection;

/**
 * Entry point for registering Types that can be referenced by the Memory Engine. </br>
 * All types provided into the Registry must respect the following.
 * <ul>
 *     <li>Nonnull Class Reference</li>
 *     <li>Respects the underlying Registry's declaration rules</li>
 * </ul>
 */
public interface TypeRegistry {

    /**
     * Convenience method for Registration. Implementations may or may not delay projection registry until the
     * underlying namespace is declared.
     * @param klazzes Nonnull Collection of valid Class references the Engine should be able to map to
     * @throws MappingException If any of the provided klazzes are invalid.
     */
    void registerAll(Collection<Class<?>> klazzes) throws MappingException;

    /**
     * Convenience method for Registration. Implementations may or may not delay projection registry until the
     * underlying namespace is declared.
     * @param klazz Nonnull Class reference to register.
     * @throws MappingException If the provided klazz is invalid.
     */
    void registerType(Class<?> klazz) throws MappingException;

    /**
     * @param klazz Nonull Interface reference to register.
     * @throws MappingException If the provided klazz is invalid.
     */
    void registerProjection(Class<?> klazz) throws MappingException;

    /**
     * This method should not attempt to register the provided klazz if it fails to find.
     *
     * @param klazz Nonnull Class reference to check.
     * @return true if the Registry contains the registration for the klazz.
     */
    boolean containsRegistration(Class<?> klazz);

}
