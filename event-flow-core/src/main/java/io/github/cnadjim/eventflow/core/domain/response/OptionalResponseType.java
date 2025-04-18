package io.github.cnadjim.eventflow.core.domain.response;

import java.util.Optional;

import static java.util.Objects.isNull;

public record OptionalResponseType<R>(Class<R> responseType) implements ResponseType<Optional<R>> {

    @Override
    public Optional<R> convert(Object response) {
        if (isNull(response)) {
            return Optional.empty();
        }

        if (responseType.isAssignableFrom(response.getClass())) {
            return Optional.of(responseType.cast(response));
        }

        return Optional.empty();
    }
}
