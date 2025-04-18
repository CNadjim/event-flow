package io.github.cnadjim.eventflow.core.spi;

import io.github.cnadjim.eventflow.core.domain.error.Error;
import io.github.cnadjim.eventflow.core.domain.error.InternalServerError;
import io.github.cnadjim.eventflow.core.domain.exception.EventFlowException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.time.Instant;
import java.util.Optional;

import static java.util.Objects.isNull;

/**
 * Service Provider Interface for error conversion.
 * This functional interface is responsible for converting exceptions into domain-specific error objects.
 * Implementations can provide custom error conversion logic for specific exception types.
 */
@FunctionalInterface
public interface ErrorConverter {

    /**
     * Attempts to convert a throwable into a domain-specific error.
     * Implementations should return an empty Optional if they cannot handle the given throwable.
     *
     * @param throwable The throwable to convert
     * @return An Optional containing the converted Error, or empty if conversion is not possible
     */
    Optional<Error> tryConvert(Throwable throwable);

    /**
     * Converts a throwable into a domain-specific error.
     * This method first tries to use the custom conversion logic, and falls back to the default conversion
     * if the custom conversion returns empty or throws an exception.
     *
     * @param throwable The throwable to convert
     * @return The converted Error
     */
    default Error convert(Throwable throwable) {
        try {
            return Optional.ofNullable(throwable).flatMap(this::tryConvert).orElseGet(() -> defaultConvert(throwable));
        } catch (Exception ignored) {
            return defaultConvert(throwable);
        }
    }

    /**
     * Provides a default conversion for throwable that cannot be handled by the custom conversion logic.
     *
     * @param throwable The throwable to convert
     * @return The converted Error
     */
    default Error defaultConvert(Throwable throwable) {
        return fromThrowable(throwable);
    }

    /**
     * Static utility method to convert a throwable into an Error.
     * If the throwable is an EventFlowException with an error, that error is returned.
     * Otherwise, a new InternalServerError is created with information from the throwable.
     *
     * @param throwable The throwable to convert
     * @return The converted Error, or null if the throwable is null
     */
    static Error fromThrowable(Throwable throwable) {
        if (isNull(throwable)) {
            return null;
        }

        if (throwable instanceof EventFlowException eventFlowException) {
            if (eventFlowException.hasError()) {
                return eventFlowException.getError();
            }
        }

        return new InternalServerError(
                Instant.now(),
                Optional.of(throwable)
                        .map(ExceptionUtils::getRootCause)
                        .map(Throwable::getMessage)
                        .map(String::trim)
                        .filter(StringUtils::isNotBlank)
                        .orElseGet(() -> ExceptionUtils.getRootCauseMessage(throwable)),
                ExceptionUtils.getRootCauseStackTraceList(throwable)
        );
    }
}
