package io.github.cnadjim.eventflow.core.stub;

import io.github.cnadjim.eventflow.annotation.Stub;
import io.github.cnadjim.eventflow.core.domain.error.Error;
import io.github.cnadjim.eventflow.core.port.ErrorConverter;

import java.util.Optional;

@Stub
public class DefaultErrorConverter implements ErrorConverter {

    @Override
    public Optional<Error> tryConvert(Throwable throwable) {
        return Optional.empty();
    }
}
