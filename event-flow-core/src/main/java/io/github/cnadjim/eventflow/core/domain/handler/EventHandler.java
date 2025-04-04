package io.github.cnadjim.eventflow.core.domain.handler;

import io.github.cnadjim.eventflow.core.domain.Event;
import io.github.cnadjim.eventflow.core.domain.exception.HandlerExecutionException;

import java.lang.reflect.Method;

public interface EventHandler extends Handler {
    void onEvent(Event event) throws HandlerExecutionException;

    static EventHandler create(Class<?> payloadClass, Object instance, Method method) {
        return new EventHandler() {

            @Override
            public Class<?> payloadClass() {
                return payloadClass;
            }

            @Override
            public void onEvent(Event event) throws HandlerExecutionException {
                invoke(instance, method, event.payload());
            }
        };
    }
}
