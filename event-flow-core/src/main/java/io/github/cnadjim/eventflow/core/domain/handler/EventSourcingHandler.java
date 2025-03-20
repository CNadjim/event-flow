package io.github.cnadjim.eventflow.core.domain.handler;

import io.github.cnadjim.eventflow.core.domain.AggregateWrapper;
import io.github.cnadjim.eventflow.core.domain.EventWrapper;
import io.github.cnadjim.eventflow.core.domain.exception.handler.EventSourcingHandlerExecutionException;

import java.lang.reflect.Method;

@FunctionalInterface
public interface EventSourcingHandler extends HandlerInvoker {
    AggregateWrapper apply(EventWrapper event, AggregateWrapper aggregate) throws EventSourcingHandlerExecutionException;

    static EventSourcingHandler create(Object instance, Method method){
        return (event, aggregate) -> {
            final Object result = HandlerInvoker.invoke(instance, method, event.payload(), aggregate.payload());
            return  AggregateWrapper.upgrade(aggregate.version() + 1, result);
        };
    }
}
