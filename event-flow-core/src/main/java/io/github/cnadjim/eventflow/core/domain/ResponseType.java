package io.github.cnadjim.eventflow.core.domain;


import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public interface ResponseType<R> {

    R convert(Object response);

    static <R> ResponseType<R> instanceOf(Class<R> responseType) {
        return new InstanceResponseType<>(responseType);
    }

    static <R> ResponseType<List<R>> listOf(Class<R> responseType) {
        return new CollectionResponseType<>(responseType);
    }

    static <R> ResponseType<Optional<R>> optionalOf(Class<R> responseType) {
        return new OptionalResponseType<>(responseType);
    }


    static <R> ResponseType<Page<R>> pageOf(Class<R> responseType) {
        return new PageResponseType<>(responseType);
    }

    record InstanceResponseType<R>(Class<R> type) implements ResponseType<R> {

        @Override
        public R convert(Object response) {
            if (response == null) {
                return null;
            }

            if (type.isAssignableFrom(response.getClass())) {
                return type.cast(response);
            }

            throw new IllegalArgumentException("Cannot convert response to " + type.getName());
        }
    }

    class CollectionResponseType<R> implements ResponseType<List<R>> {
        private final Class<R> responseType;

        public CollectionResponseType(Class<R> responseType) {
            this.responseType = responseType;
        }

        @Override
        public List<R> convert(Object response) {
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

    class OptionalResponseType<R> implements ResponseType<Optional<R>> {
        private final Class<R> responseType;

        public OptionalResponseType(Class<R> responseType) {
            this.responseType = responseType;
        }

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

    class PageResponseType<R> implements ResponseType<Page<R>> {
        private final Class<R> responseType;

        public PageResponseType(Class<R> responseType) {
            this.responseType = responseType;
        }

        @Override
        public Page<R> convert(Object response) {
            if (isNull(response)) {
                return new Page<>(List.of(), 0, 0, 0, 0);
            }

            if (response instanceof Page<?> pageResponse) {
                final List<R> filteredContent = pageResponse.content().stream()
                        .filter(item -> responseType.isAssignableFrom(item.getClass()))
                        .map(responseType::cast)
                        .collect(Collectors.toList());

                return new Page<>(filteredContent, pageResponse.totalItems(), pageResponse.totalPages(), pageResponse.pageNumber(), pageResponse.pageNumber());
            }

            throw new IllegalArgumentException("Cannot convert response to a paginated response of " + responseType.getName());
        }
    }
}
