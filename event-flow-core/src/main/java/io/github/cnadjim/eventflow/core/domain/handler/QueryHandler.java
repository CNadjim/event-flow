package io.github.cnadjim.eventflow.core.domain.handler;

import io.github.cnadjim.eventflow.core.domain.QueryWrapper;
import io.github.cnadjim.eventflow.core.domain.exception.handler.QueryHandlerExecutionException;

import java.lang.reflect.Method;

@FunctionalInterface
public interface QueryHandler extends HandlerInvoker {
    Object handle(QueryWrapper query) throws QueryHandlerExecutionException;

    static QueryHandler create(Object instance, Method method) {
        return (query) -> HandlerInvoker.invoke(instance, method, query.payload());
    }
}
