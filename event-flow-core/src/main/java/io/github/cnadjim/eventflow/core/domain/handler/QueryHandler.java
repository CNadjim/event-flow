package io.github.cnadjim.eventflow.core.domain.handler;

import io.github.cnadjim.eventflow.core.domain.Query;
import io.github.cnadjim.eventflow.core.domain.exception.HandlerExecutionException;

import java.lang.reflect.Method;

public interface QueryHandler extends Handler {
    Object handle(Query query) throws HandlerExecutionException;

    static QueryHandler create(Class<?> payloadClass, Object instance, Method method) {

        return new QueryHandler() {

            @Override
            public Class<?> payloadClass() {
                return payloadClass;
            }

            @Override
            public Object handle(Query query) throws HandlerExecutionException {
                return invoke(instance, method, query.payload());
            }
        };
    }
}
