package org.crain.dme4j.core.context;

import java.util.Objects;

/**
 * Default {@link ConnectionDetails} provided by the Framework. Only checks that the provided {@code location} exists.
 */
public record SimpleConnectionDetails(String location) implements ConnectionDetails {

    public SimpleConnectionDetails {
        Objects.requireNonNull(location, "Connection Location cannot be null");
        if (location.isBlank()) throw new IllegalArgumentException("Connection Location cannot be blank");
    }
}
