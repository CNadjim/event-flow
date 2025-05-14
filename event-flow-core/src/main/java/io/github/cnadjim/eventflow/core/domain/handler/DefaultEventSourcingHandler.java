package io.github.cnadjim.eventflow.core.domain.handler;

import io.github.cnadjim.eventflow.core.domain.aggregate.Aggregate;
import io.github.cnadjim.eventflow.core.domain.exception.HandlerExecutionException;
import io.github.cnadjim.eventflow.core.domain.message.Event;

import java.lang.reflect.Method;

public record DefaultEventSourcingHandler(Class<?> payloadClass, Object instance,
                                          Method method) implements EventSourcingHandler {

    @Override
    public Aggregate apply(Event event, Aggregate aggregate) throws HandlerExecutionException {
        invoke(aggregate.payload(), method, event.payload());
        return new Aggregate(aggregate.version() + 1, aggregate.payload(), aggregate.aggregateId());
    }
}
