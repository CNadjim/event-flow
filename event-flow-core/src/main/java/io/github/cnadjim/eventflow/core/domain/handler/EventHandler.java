package io.github.cnadjim.eventflow.core.domain.handler;

import io.github.cnadjim.eventflow.core.domain.Event;
import io.github.cnadjim.eventflow.core.domain.exception.handler.EventHandlerExecutionException;

import java.lang.reflect.Method;

@FunctionalInterface
public interface EventHandler extends HandlerInvoker {
    void onEvent(Event event) throws EventHandlerExecutionException;

    static EventHandler create(Object instance, Method method) {
        return (event) -> HandlerInvoker.invoke(instance, method, event.payload());
    }
}
