package io.github.cnadjim.eventflow.core.domain.handler;

import io.github.cnadjim.eventflow.core.domain.message.Query;
import io.github.cnadjim.eventflow.core.domain.exception.HandlerExecutionException;

import java.lang.reflect.Method;

public interface QueryHandler extends Handler {

    Object handle(Query query) throws HandlerExecutionException;
}
