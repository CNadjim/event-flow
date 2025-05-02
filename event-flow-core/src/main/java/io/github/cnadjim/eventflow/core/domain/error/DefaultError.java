package io.github.cnadjim.eventflow.core.domain.error;

import io.github.cnadjim.eventflow.core.domain.exception.BadArgumentException;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;

import static java.util.Objects.isNull;

/**
 * Default implementation of the {@link Error} interface.
 * This record represents an error in the event flow system with timestamp, status, reason, message, and details.
 *
 * @param timestamp    The timestamp when the error occurred
 * @param status       The status code associated with the error
 * @param reasonPhrase A short phrase describing the reason for the error
 * @param message      A detailed message describing the error
 * @param details      Additional details about the error
 */
public record DefaultError(Instant timestamp,
                           Integer status,
                           String reasonPhrase,
                           String message,
                           Object details) implements Error {

    /**
     * Compact constructor that validates the record components.
     * 
     * @throws BadArgumentException if timestamp is null, status is null, reasonPhrase is blank, or message is blank
     */
    public DefaultError {
        if (isNull(timestamp)) throw new BadArgumentException("Timestamp cannot be null");
        if (isNull(status)) throw new BadArgumentException("Status cannot be null");
        if (StringUtils.isBlank(reasonPhrase)) throw new BadArgumentException("ReasonPhrase cannot be blank");
        if (StringUtils.isBlank(message)) throw new BadArgumentException("Message cannot be blank");
    }

    /**
     * Creates a new DefaultError with the current timestamp and the specified parameters.
     *
     * @param status       The status code associated with the error
     * @param reasonPhrase A short phrase describing the reason for the error
     * @param message      A detailed message describing the error
     * @param details      Additional details about the error
     * @return A new DefaultError instance
     * @throws BadArgumentException if status is null, reasonPhrase is blank, or message is blank
     */
    public static DefaultError create(Integer status, String reasonPhrase, String message, Object details) {
        return new DefaultError(Instant.now(), status, reasonPhrase, message, details);
    }
}
