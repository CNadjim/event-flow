package io.github.cnadjim.eventflow.spring.starter.pagination;

import io.github.cnadjim.eventflow.core.domain.ResponseType;
import io.github.cnadjim.eventflow.spring.starter.exception.RestException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

/**
 * Implémentation de ResponseType spécifique pour les pages Spring
 */
public class SpringPageResponseType<T> implements ResponseType<Page<T>> {
    private final Class<T> elementType;

    public SpringPageResponseType(Class<T> elementType) {
        this.elementType = elementType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<Page<T>> responseMessagePayloadType() {
        return (Class<Page<T>>) (Class<?>) Page.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Page<T> convert(Object response) {
        if (response == null) {
            return Page.empty();
        }

        if (response instanceof Page<?> page) {
            return (Page<T>) page;
        }

        throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot convert response to a spring page of " + elementType.getName());
    }
}
