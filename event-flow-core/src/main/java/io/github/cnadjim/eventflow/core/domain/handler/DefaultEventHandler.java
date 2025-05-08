package io.github.cnadjim.eventflow.core.domain.handler;

import io.github.cnadjim.eventflow.core.domain.exception.HandlerExecutionException;
import io.github.cnadjim.eventflow.core.domain.message.Event;

import java.lang.reflect.Method;

public record DefaultEventHandler(Class<?> payloadClass, Object instance, Method method) implements EventHandler {

    @Override
    public void onEvent(Event event) throws HandlerExecutionException {
        invoke(instance, method, event.payload());
    }
}
