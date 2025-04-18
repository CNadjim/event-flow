package io.github.cnadjim.eventflow.core.domain.response;

import java.util.Optional;

public record OptionalResponseType<R>(Class<R> responseType) implements ResponseType<Optional<R>> {

    @Override
    public Optional<R> convert(Object response) {
        if (response == null) {
            return Optional.empty();
        }

        if (responseType.isAssignableFrom(response.getClass())) {
            return Optional.of(responseType.cast(response));
        }

        return Optional.empty();
    }
}
