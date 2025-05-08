package io.github.cnadjim.eventflow.core.domain.handler;

import io.github.cnadjim.eventflow.core.domain.exception.HandlerExecutionException;
import io.github.cnadjim.eventflow.core.domain.message.Event;
import io.github.cnadjim.eventflow.core.domain.message.Query;

import java.lang.reflect.Method;

public record DefaultQueryHandler(Class<?> payloadClass, Object instance, Method method) implements QueryHandler {

    @Override
    public Object handle(Query query) throws HandlerExecutionException {
        return invoke(instance, method, query.payload());
    }
}
