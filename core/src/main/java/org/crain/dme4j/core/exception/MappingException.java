package org.crain.dme4j.core.exception;

/**
 * MappingException(s) cover the following situations.
 * <ul>
 *     <li>Invalid Concrete Class provided to the {@link org.crain.dme4j.core.context.TypeRegistry TypeRegistry}</li>
 *     <li>Invalid Projection Interface provided to the {@link org.crain.dme4j.core.context.TypeRegistry TypeRegistry}</li>
 *     <li>Invalid override of a definition in the {@link org.crain.dme4j.core.context.TypeRegistry TypeRegistry}</li>
 *     <li>Invalid dynamic creation of a {@link org.crain.dme4j.core.types.Struct Struct} instance.</li>
 *     <li>Failure to find a Type definition</li>
 * </ul>
 * These exceptions do not invalidate a {@link org.crain.dme4j.core.context.TypeRegistry TypeRegistry}, but hint that
 * the expected state of the Registry is invalid.
 */
public sealed abstract class MappingException extends DmeCoreModuleException
        permits InvalidStructureException, IllegalTypeException, DuplicateException, MissingMappingException {

    MappingException(String message) {
        super(message);
    }

    MappingException(Throwable cause) {
        super(cause);
    }

    MappingException(String message, Throwable cause) {
        super(message, cause);
    }

}
