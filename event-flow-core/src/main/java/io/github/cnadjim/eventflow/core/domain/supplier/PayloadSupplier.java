package io.github.cnadjim.eventflow.core.domain.supplier;

import io.github.cnadjim.eventflow.annotation.Aggregate;

import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * Functional interface for supplying payload objects.
 * This interface provides access to a payload object and utility methods for working with it.
 */
@FunctionalInterface
public interface PayloadSupplier {

    /**
     * Returns the payload object.
     *
     * @return The payload object
     */
    Object payload();

    /**
     * Returns the class of the payload object.
     *
     * @return The class of the payload object
     */
    default Class<?> payloadClass() {
        return payload().getClass();
    }

    /**
     * Returns the simple name of the payload class.
     *
     * @return The simple name of the payload class
     */
    default String payloadClassSimpleName() {
        return payload().getClass().getSimpleName();
    }

    /**
     * Returns an Optional containing the class of the payload object, or empty if the payload is null.
     *
     * @return An Optional containing the class of the payload object, or empty if the payload is null
     */
    default Optional<Class<?>> optionalPayloadClass() {
        return  Optional.ofNullable(payload()).map(Object::getClass);
    }

    /**
     * Finds an annotation of the specified type in the payload class.
     *
     * @param annotationClass The class of the annotation to find
     * @param <ANNOTATION> The type of the annotation
     * @return An Optional containing the annotation if found, or empty if not found or if the payload is null
     */
    default <ANNOTATION extends Annotation> Optional<ANNOTATION> findAnnotationInPayload(Class<ANNOTATION> annotationClass){
        return optionalPayloadClass().map(aclass -> aclass.getAnnotation(annotationClass));
    }

    /**
     * Finds the Aggregate annotation in the payload class.
     *
     * @return An Optional containing the Aggregate annotation if found, or empty if not found or if the payload is null
     */
    default Optional<Aggregate> findAggregateAnnotationInPayload(){
        return findAnnotationInPayload(Aggregate.class);
    }
}
