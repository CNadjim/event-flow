package io.github.cnadjim.eventflow.core.domain.supplier;

import java.util.Optional;

@FunctionalInterface
public interface PayloadSupplier {
    String PAYLOAD_FIELD = "payload";

    Object payload();

    static Optional<String> optionalPayloadClassSimpleName(Object payload) {
        return Optional.ofNullable(payload).map(Object::getClass).map(Class::getSimpleName);
    }

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
