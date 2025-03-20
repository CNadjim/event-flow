package io.github.cnadjim.eventflow.core.domain.supplier;

import io.github.cnadjim.eventflow.annotation.Topic;

import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@FunctionalInterface
public interface TopicSupplier {
    String TOPIC_FIELD = "topic";

    String topic();

    static String getTopic(Object payload) {
        return findTopic(payload).orElse(null);
    }

    static String getTopic(Class<?> clazz) {
        return findTopic(clazz).orElse(null);
    }

    static Optional<String> findTopic(Class<?> clazz) {
        final Topic payloadTopic = clazz.getAnnotation(Topic.class);

        if (nonNull(payloadTopic)) {
            return Optional.of(payloadTopic).map(Topic::value);
        }

        for (final Class<?> interfaceClass : clazz.getInterfaces()) {
            final Topic interfaceTopic = interfaceClass.getAnnotation(Topic.class);

            if (nonNull(interfaceTopic)) {
                return Optional.of(interfaceTopic).map(Topic::value);
            }
        }

        final Class<?> superClass = clazz.getSuperclass();

        if (nonNull(superClass)) {
            return findTopic(superClass);
        }

        return Optional.empty();
    }

    static Optional<String> findTopic(Object payload) {
        if (isNull(payload)) {
            return Optional.empty();
        }

        final Class<?> clazz = payload.getClass();

        return findTopic(clazz);
    }

}
