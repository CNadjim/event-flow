package io.github.cnadjim.eventflow.core.domain.handler;

import io.github.cnadjim.eventflow.core.domain.exception.handler.HandlerInvocationException;

import java.lang.reflect.Method;

public interface HandlerInvoker {

    static Object invoke(Object instance, Method method, Object... args) throws HandlerInvocationException {
        try {
            method.setAccessible(true);
            return method.invoke(instance, args);
        } catch (Exception exception) {
            throw new HandlerInvocationException(exception);
        }
    }
    
}
