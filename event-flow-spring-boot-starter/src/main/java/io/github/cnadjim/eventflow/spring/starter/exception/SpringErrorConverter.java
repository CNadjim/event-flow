package io.github.cnadjim.eventflow.spring.starter.exception;

import io.github.cnadjim.eventflow.core.domain.error.Error;
import io.github.cnadjim.eventflow.core.domain.error.DefaultError;
import io.github.cnadjim.eventflow.core.port.ErrorConverter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static java.util.Objects.nonNull;

public class SpringErrorConverter implements ErrorConverter {

    @Override
    public Optional<Error> tryConvert(Throwable throwable) {
        final Throwable exception = ExceptionUtils.getRootCause(throwable);
        Error error = null;

        if (exception instanceof RestException restException) {
            final HttpStatus httpStatus = HttpStatus.resolve(restException.getStatusCode().value());

            if (nonNull(httpStatus)) {
                error = DefaultError.create(httpStatus.value(), httpStatus.getReasonPhrase(), restException.getReason(), ExceptionUtils.getRootCauseStackTraceList(exception));
            }
        }

        return Optional.ofNullable(error);
    }
}
