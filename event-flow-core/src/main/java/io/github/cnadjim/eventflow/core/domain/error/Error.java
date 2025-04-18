package io.github.cnadjim.eventflow.core.domain.error;

import java.time.Instant;

/**
 * Interface representing an error in the event flow system.
 * Provides access to error information such as timestamp, status, reason, message, and details.
 */
public interface Error {

    /**
     * Returns the timestamp when the error occurred.
     *
     * @return The timestamp as an Instant
     */
    Instant timestamp();

    /**
     * Returns the status code associated with the error.
     * This is typically an HTTP status code or a custom error code.
     *
     * @return The status code
     */
    Integer status();

    /**
     * Returns a short phrase describing the reason for the error.
     *
     * @return The reason phrase
     */
    String reasonPhrase();

    /**
     * Returns a detailed message describing the error.
     *
     * @return The error message
     */
    String message();

    /**
     * Returns additional details about the error.
     * This can be any object containing more specific information about the error.
     *
     * @return The error details
     */
    Object details();
}
