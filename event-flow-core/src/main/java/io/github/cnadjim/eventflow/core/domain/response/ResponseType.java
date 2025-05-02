package io.github.cnadjim.eventflow.core.domain.response;


import io.github.cnadjim.eventflow.core.domain.pagination.Page;

import java.util.Collection;
import java.util.Optional;

/**
 * Functional interface for converting response objects to specific types.
 * This interface is used to define how response objects should be converted to the desired return type.
 *
 * @param <R> The target type to which the response will be converted
 */
@FunctionalInterface
public interface ResponseType<R> {

    /**
     * Converts a response object to the target type.
     *
     * @param response The response objects to convert
     * @return The converted response of type R
     */
    R convert(Object response);

    /**
     * Creates a ResponseType that converts the response to an instance of the specified class.
     *
     * @param responseType The class of the target type
     * @param <R> The target type
     * @return A ResponseType that converts to an instance of the specified class
     */
    static <R> ResponseType<R> instanceOf(Class<R> responseType) {
        return new InstanceResponseType<>(responseType);
    }

    /**
     * Creates a ResponseType that converts the response to a collection of instances of the specified class.
     *
     * @param responseType The class of the elements in the collection
     * @param <R> The type of elements in the collection
     * @return A ResponseType that converts to a collection of instances of the specified class
     */
    static <R> ResponseType<Collection<R>> collectionOf(Class<R> responseType) {
        return new CollectionResponseType<>(responseType);
    }

    /**
     * Creates a ResponseType that converts the response to an Optional containing an instance of the specified class.
     *
     * @param responseType The class of the element in the Optional
     * @param <R> The type of the element in the Optional
     * @return A ResponseType that converts to an Optional containing an instance of the specified class
     */
    static <R> ResponseType<Optional<R>> optionalOf(Class<R> responseType) {
        return new OptionalResponseType<>(responseType);
    }

    /**
     * Creates a ResponseType that converts the response to a Page containing instances of the specified class.
     *
     * @param responseType The class of the elements in the Page
     * @param <R> The type of elements in the Page
     * @return A ResponseType that converts to a Page containing instances of the specified class
     */
    static <R> ResponseType<Page<R>> pageOf(Class<R> responseType) {
        return new PageResponseType<>(responseType);
    }
}
