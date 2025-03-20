package io.github.cnadjim.eventflow.core.domain.supplier;

import io.github.cnadjim.eventflow.annotation.AggregateId;

import java.lang.reflect.Field;
import java.util.Optional;

import static java.util.Objects.isNull;

@FunctionalInterface
public interface AggregateIdSupplier {

    String AGGREGATE_ID_FIELD = "aggregateId";

    String aggregateId();

    static String create() {
        return IdSupplier.create();
    }

    static String getAggregateId(Object payload) {
        return findAggregateId(payload).orElse(null);
    }

    static Optional<String> findAggregateId(Object payload) {
        Optional<String> optionalAggregateId = Optional.empty();

        if (isNull(payload)) {
            return optionalAggregateId;
        }

        final Class<?> payloadClass = payload.getClass();

        for (Field field : payloadClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(AggregateId.class)) {
                field.setAccessible(true);
                try {
                    Object value = field.get(payload);
                    return Optional.ofNullable(value).map(Object::toString);
                } catch (Exception ignored) {

                }
            }
        }

        return optionalAggregateId;
    }
}
