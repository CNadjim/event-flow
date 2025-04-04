package io.github.cnadjim.eventflow.core.domain.supplier;

import java.util.Optional;

@FunctionalInterface
public interface PayloadSupplier {

    Object payload();

    default Class<?> payloadClass() {
        return payload().getClass();
    }

    default String payloadClassSimpleName() {
        return payload().getClass().getSimpleName();
    }

    default Optional<Class<?>> optionalPayloadClass() {
        return  Optional.ofNullable(payload()).map(Object::getClass);
    }

}
