package io.github.cnadjim.eventflow.core.domain.response;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public record CollectionResponseType<R>(Class<R> responseType) implements ResponseType<Collection<R>> {

    @Override
    public Collection<R> convert(Object response) {
        if (response == null) {
            return List.of();
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
