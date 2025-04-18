package io.github.cnadjim.eventflow.core.api;

import io.github.cnadjim.eventflow.annotation.UseCase;
import io.github.cnadjim.eventflow.core.domain.handler.Handler;

import java.util.Collection;
import java.util.Collections;

/**
 * {@code ScanObject} defines the use case for scanning an object instance for handler methods.
 * It provides methods to scan for all handlers or to filter handlers based on their class.
 */
@UseCase
public interface ScanObject {

    /**
     * Scans an object instance for handler methods and returns a collection of {@link Handler} instances.
     *
     * @param instance The object instance to scan.
     * @return A collection of {@link Handler} instances found in the object.
     */
    Collection<Handler> scan(Object instance);

    /**
     * Scans an object instance for handler methods and returns a collection of {@link Handler} instances
     * that are instances of the specified {@code handlerClass}.
     *
     * @param instance     The object instance to scan.
     * @param handlerClass The class of the handler to filter for.
     * @param <HANDLER>    The type of the handler class.
     * @return A collection of {@link Handler} instances that are instances of the specified {@code handlerClass}.
     */
    default <HANDLER extends Handler> Collection<Handler> scan(Object instance, Class<HANDLER> handlerClass) {
        return scan(instance, Collections.singleton(handlerClass));
    }

    /**
     * Scans an object instance for handler methods and returns a collection of {@link Handler} instances
     * that are instances of the handler classes in the specified {@code handlerClasses} collection.
     *
     * @param instance       The object instance to scan.
     * @param handlerClasses A collection of handler classes to filter for.
     * @return A collection of {@link Handler} instances that are instances of the handler classes
     * in the specified {@code handlerClasses} collection.
     */
    default Collection<Handler> scan(Object instance, Collection<Class<? extends Handler>> handlerClasses) {
        return scan(instance)
                .stream()
                .filter(handler -> isHandlerInstance(handler, handlerClasses))
                .toList();
    }

    /**
     * Checks if a handler is an instance of a specific handler class.
     *
     * @param handler      The handler to check.
     * @param handlerClass The handler class to check against.
     * @return {@code true} if the handler is an instance of the handler class, {@code false} otherwise.
     */
    private static boolean isHandlerInstance(Handler handler, Class<? extends Handler> handlerClass) {
        return handlerClass.isInstance(handler);
    }

    /**
     * Checks if a handler is an instance of the handler classes in a collection.
     *
     * @param handler        The handler to check.
     * @param handlerClasses A collection of handler classes to check against.
     * @return {@code true} if the handler is an instance of the handler classes in the collection,
     * {@code false} otherwise.
     */
    private static boolean isHandlerInstance(Handler handler, Collection<Class<? extends Handler>> handlerClasses) {
        return handlerClasses.stream().anyMatch(handlerClass -> isHandlerInstance(handler, handlerClass));
    }
}
