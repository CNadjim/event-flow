package io.github.cnadjim.eventflow.core.domain.response;

import io.github.cnadjim.eventflow.core.domain.exception.BadArgumentException;

import static java.util.Objects.isNull;

public record InstanceResponseType<R>(Class<R> responseType) implements ResponseType<R> {

    @Override
    public R convert(Object response) {
        if (isNull(response)) {
            return null;
        }

        if (responseType.isAssignableFrom(response.getClass())) {
            return responseType.cast(response);
        }

        throw new BadArgumentException("Cannot convert response to " + responseType.getName());
    }
}
