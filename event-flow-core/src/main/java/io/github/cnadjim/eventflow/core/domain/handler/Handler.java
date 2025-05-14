package io.github.cnadjim.eventflow.core.domain.handler;


import io.github.cnadjim.eventflow.core.domain.exception.HandlerExecutionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Optional;

public interface Handler {

    Class<?> payloadClass();

    Object instance();

    default Object aggregateInstance(){
        try {
            final Class<?> clazz = instance().getClass();
            final Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception exception) {
            return instance();
        }
    }

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
