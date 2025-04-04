package io.github.cnadjim.eventflow.core.domain.handler;

import io.github.cnadjim.eventflow.core.domain.Aggregate;
import io.github.cnadjim.eventflow.core.domain.Event;
import io.github.cnadjim.eventflow.core.domain.exception.HandlerExecutionException;

import java.lang.reflect.Method;

public interface EventSourcingHandler extends Handler {
    Aggregate apply(Event event, Aggregate aggregate) throws HandlerExecutionException;

    static EventSourcingHandler create(Class<?> payloadClass, Object instance, Method method) {
        return new EventSourcingHandler() {
            @Override
            public Class<?> payloadClass() {
                return payloadClass;
            }

            @Override
            public Aggregate apply(Event event, Aggregate aggregate) throws HandlerExecutionException {
                final Object result = invoke(instance, method, event.payload(), aggregate.payload());
                return Aggregate.upgrade(aggregate.version() + 1, result);
            }
        };
    }
}
