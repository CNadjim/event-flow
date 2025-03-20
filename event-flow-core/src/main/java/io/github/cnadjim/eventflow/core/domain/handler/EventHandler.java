package io.github.cnadjim.eventflow.core.domain.handler;

import io.github.cnadjim.eventflow.core.domain.EventWrapper;
import io.github.cnadjim.eventflow.core.domain.exception.handler.EventHandlerExecutionException;

import java.lang.reflect.Method;

@FunctionalInterface
public interface EventHandler extends HandlerInvoker {
    void onEvent(EventWrapper event) throws EventHandlerExecutionException;

    static EventHandler create(Object instance, Method method) {
        return (event) -> HandlerInvoker.invoke(instance, method, event.payload());
    }
}
