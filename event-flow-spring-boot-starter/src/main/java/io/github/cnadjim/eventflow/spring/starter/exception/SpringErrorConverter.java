package io.github.cnadjim.eventflow.spring.starter.exception;

import io.github.cnadjim.eventflow.core.domain.Error;
import io.github.cnadjim.eventflow.core.domain.error.DefaultError;
import io.github.cnadjim.eventflow.core.spi.ErrorConverter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static java.util.Objects.nonNull;

public class SpringErrorConverter implements ErrorConverter {

    @Override
    public Error convert(Throwable throwable) {

        final Throwable exception = ExceptionUtils.getRootCause(throwable);

        if (exception instanceof RestException restException) {
            final HttpStatus httpStatus = HttpStatus.resolve(restException.getStatusCode().value());

            if (nonNull(httpStatus)) {
                return DefaultError.create(httpStatus.value(), httpStatus.getReasonPhrase(), restException.getReason(), ExceptionUtils.getRootCauseStackTraceList(exception));
            }
        }

        return Error.fromThrowable(throwable);
    }
}
