package io.github.cnadjim.eventflow.core.domain;


import io.github.cnadjim.eventflow.core.domain.exception.EventFlowIllegalArgumentException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Interface describing the expected response type for a query.
 * Inspired by Axon Framework's ResponseType.
 *
 * @param <R> The response class contained in this response type
 */
public interface ResponseType<R> {

    /**
     * Returns the expected response class.
     */
    Class<R> responseMessagePayloadType();

    /**
     * Converts the response from its serialized form to the expected response type.
     */
    R convert(Object response);

    /**
     * Returns a ResponseType for a single instance of type R.
     */
    static <R> ResponseType<R> instanceOf(Class<R> responseType) {
        return new InstanceResponseType<>(responseType);
    }

    /**
     * Returns a ResponseType for a collection of instances of type R.
     */
    static <R> ResponseType<List<R>> listOf(Class<R> responseType) {
        return new CollectionResponseType<>(responseType);
    }

    /**
     * Returns a ResponseType for an optional of type R.
     */
    static <R> ResponseType<Optional<R>> optionalOf(Class<R> responseType) {
        return new OptionalResponseType<>(responseType);
    }

    /**
     * Implementation of ResponseType for a single instance.
     */
    class InstanceResponseType<R> implements ResponseType<R> {
        private final Class<R> responseType;

        public InstanceResponseType(Class<R> responseType) {
            this.responseType = responseType;
        }

        @Override
        public Class<R> responseMessagePayloadType() {
            return responseType;
        }

        @Override
        @SuppressWarnings("unchecked")
        public R convert(Object response) {
            if (response == null) {
                return null;
            }

            if (responseType.isAssignableFrom(response.getClass())) {
                return (R) response;
            }

            throw new EventFlowIllegalArgumentException("Cannot convert response to " + responseType.getName());
        }
    }

    /**
     * Implementation of ResponseType for a collection of instances.
     */
    class CollectionResponseType<R> implements ResponseType<List<R>> {
        private final Class<R> responseType;

        public CollectionResponseType(Class<R> responseType) {
            this.responseType = responseType;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Class<List<R>> responseMessagePayloadType() {
            return (Class<List<R>>) (Class<?>) List.class;
        }


        @Override
        @SuppressWarnings("unchecked")
        public List<R> convert(Object response) {
            if (response == null) {
                return List.of();
            }

            if (response instanceof Collection) {
                return ((Collection<?>) response).stream()
                        .filter(item -> responseType.isAssignableFrom(item.getClass()))
                        .map(item -> (R) item)
                        .collect(Collectors.toList());
            }

            throw new EventFlowIllegalArgumentException("Cannot convert response to a collection of " + responseType.getName());
        }
    }

    /**
     * Implementation of ResponseType for an optional instance.
     */
    class OptionalResponseType<R> implements ResponseType<Optional<R>> {
        private final Class<R> responseType;

        public OptionalResponseType(Class<R> responseType) {
            this.responseType = responseType;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Class<Optional<R>> responseMessagePayloadType() {
            return (Class<Optional<R>>) (Class<?>) Optional.class;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Optional<R> convert(Object response) {
            if (response == null) {
                return Optional.empty();
            }

            if (responseType.isAssignableFrom(response.getClass())) {
                return Optional.of((R) response);
            }

            return Optional.empty();
        }
    }
}
