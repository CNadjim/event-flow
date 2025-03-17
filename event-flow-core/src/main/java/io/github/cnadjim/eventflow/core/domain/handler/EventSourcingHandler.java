package io.github.cnadjim.eventflow.core.domain.handler;

import io.github.cnadjim.eventflow.core.domain.Aggregate;
import io.github.cnadjim.eventflow.core.domain.Event;
import io.github.cnadjim.eventflow.core.domain.exception.handler.EventSourcingHandlerExecutionException;

import java.lang.reflect.Method;

@FunctionalInterface
public interface EventSourcingHandler extends HandlerInvoker {
    Aggregate apply(Event event, Aggregate aggregate) throws EventSourcingHandlerExecutionException;

    static EventSourcingHandler create(Object instance, Method method){
        return (event, aggregate) -> {
            final Object result = HandlerInvoker.invoke(instance, method, event.payload(), aggregate.payload());
            return new Aggregate(aggregate.version() + 1, result, aggregate.aggregateId());
        };
    }
}
