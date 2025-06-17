package org.crain.dme4j.core.exception;

/**
 * Super class of all DME4J Exceptions. We break Exceptions into the following categories
 * <ul>
 *     <li> {@link MappingException Mapping} </li>
 *     <li> {@link QueryException Queries} </li>
 *     <li> {@link EngineException Engine} </li>
 *     <li> {@link CustomizableDmeException Custom}</li>
 * </ul>
 */
public sealed abstract class DmeCoreModuleException extends Exception permits
        MappingException, QueryException, EngineException, CustomizableDmeException {

    //region Constructors
    DmeCoreModuleException(String message) {
        super(message);
    }

    DmeCoreModuleException(Throwable cause) {
        super(cause);
    }

    DmeCoreModuleException(String message, Throwable cause) {
        super(message, cause);
    }
    //endregion
}
