package io.github.cnadjim.eventflow.core.spi;

import io.github.cnadjim.eventflow.core.domain.Error;

public interface ErrorConverter {

    default Error convert(Throwable throwable) {
        return Error.fromThrowable(throwable);
    }
}
