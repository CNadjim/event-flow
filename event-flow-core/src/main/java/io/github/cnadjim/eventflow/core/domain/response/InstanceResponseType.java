package io.github.cnadjim.eventflow.core.domain.response;

import static java.util.Objects.isNull;

public record InstanceResponseType<R>(Class<R> type) implements ResponseType<R> {

    @Override
    public R convert(Object response) {
        if (isNull(response)) {
            return null;
        }

        if (type.isAssignableFrom(response.getClass())) {
            return type.cast(response);
        }

        throw new IllegalArgumentException("Cannot convert response to " + type.getName());
    }
}
