package io.github.cnadjim.eventflow.core.domain;

import io.github.cnadjim.eventflow.core.domain.error.InternalServerError;
import io.github.cnadjim.eventflow.core.domain.exception.EventFlowException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.time.Instant;
import java.util.Optional;

import static java.util.Objects.isNull;

public interface Error {

    Instant timestamp();

    Integer status();

    String reasonPhrase();

    String message();

    Object details();

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
