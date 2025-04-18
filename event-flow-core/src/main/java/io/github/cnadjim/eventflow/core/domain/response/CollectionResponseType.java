package io.github.cnadjim.eventflow.core.domain.response;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public record CollectionResponseType<R>(Class<R> responseType) implements ResponseType<Collection<R>> {

    @Override
    public Collection<R> convert(Object response) {
        if (isNull(response)) {
            return Collections.emptyList();
        }

        if (response instanceof Collection) {
            return ((Collection<?>) response).stream()
                    .filter(item -> responseType.isAssignableFrom(item.getClass()))
                    .map(responseType::cast)
                    .collect(Collectors.toList());
        }

        throw new IllegalArgumentException("Cannot convert response to a collection of " + responseType.getName());
    }
}
