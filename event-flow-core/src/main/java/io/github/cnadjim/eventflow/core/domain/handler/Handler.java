package io.github.cnadjim.eventflow.core.domain.handler;


import io.github.cnadjim.eventflow.core.domain.exception.HandlerExecutionException;

import java.lang.reflect.Method;

public interface Handler {

    Class<?> payloadClass();

    default boolean canHandle(Class<?> payloadClass) {
        return payloadClass.isAssignableFrom(payloadClass());
    }

    default Object invoke(Object instance, Method method, Object... args) throws HandlerExecutionException {
        try {
            method.setAccessible(true);
            return method.invoke(instance, args);
        } catch (Exception exception) {
            throw new HandlerExecutionException(exception);
        }
    }

}
