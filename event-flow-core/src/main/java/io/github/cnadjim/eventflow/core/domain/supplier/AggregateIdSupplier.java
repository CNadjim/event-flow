package io.github.cnadjim.eventflow.core.domain.supplier;

import io.github.cnadjim.eventflow.annotation.AggregateId;

import java.lang.reflect.Field;
import java.util.Optional;

import static java.util.Objects.isNull;

/**
 * Functional interface for supplying aggregate identifiers.
 * This interface is used to provide unique identifiers for aggregates in the event-sourced system.
 * Implementations of this interface can generate or retrieve aggregate IDs.
 */
@FunctionalInterface
public interface AggregateIdSupplier {

    /**
     * Returns the aggregate identifier.
     *
     * @return The aggregate ID as a string
     */
    String aggregateId();

    /**
     * Creates a new unique aggregate identifier.
     *
     * @return A newly generated unique aggregate ID
     */
    static String create() {
        return IdSupplier.create();
    }

    /**
     * Extracts the aggregate ID from the given payload object.
     * This method looks for fields annotated with {@link AggregateId} in the payload.
     *
     * @param payload The object from which to extract the aggregate ID
     * @return The aggregate ID if found, or null if not found
     */
    static String getAggregateId(Object payload) {
        return findAggregateId(payload).orElse(null);
    }

    /**
     * Finds the aggregate ID in the given payload object.
     * This method searches for fields annotated with {@link AggregateId} in the payload
     * and returns the value of the first such field found.
     *
     * @param payload The object in which to search for the aggregate ID
     * @return An Optional containing the aggregate ID if found, or empty if not found or if payload is null
     */
    static Optional<String> findAggregateId(Object payload) {
        Optional<String> optionalAggregateId = Optional.empty();

        if (isNull(payload)) {
            return optionalAggregateId;
        }

        final Class<?> payloadClass = payload.getClass();

        for (Field field : payloadClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(AggregateId.class)) {
                field.setAccessible(true);
                try {
                    Object value = field.get(payload);
                    return Optional.ofNullable(value).map(Object::toString);
                } catch (Exception ignored) {
                    // Exception is ignored as we continue searching for other annotated fields
                }
            }
        }

        return optionalAggregateId;
    }

}
