package io.github.cnadjim.eventflow.core.domain.supplier;

import java.util.UUID;

/**
 * Interface for supplying identifiers.
 * This interface provides access to an identifier and a utility method for creating new identifiers.
 */
public interface IdSupplier {

    /**
     * Returns the identifier.
     *
     * @return The identifier as a string
     */
    String id();

    /**
     * Creates a new unique identifier using UUID.
     *
     * @return A newly generated unique identifier
     */
    static String create() {
        return UUID.randomUUID().toString();
    }
}
