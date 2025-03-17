package io.github.cnadjim.eventflow.core.api;

import io.github.cnadjim.eventflow.core.domain.handler.CommandHandler;
import io.github.cnadjim.eventflow.core.domain.handler.EventHandler;
import io.github.cnadjim.eventflow.core.domain.handler.EventSourcingHandler;
import io.github.cnadjim.eventflow.core.domain.handler.QueryHandler;

public interface RegisterHandler {

    void registerCommandHandler(Class<?> messagePayloadClass, CommandHandler commandHandler);

    void registerEventHandler(Class<?> messagePayloadClass, EventHandler eventHandler);

    void registerQueryHandler(Class<?> messagePayloadClass, QueryHandler queryHandler);

    void registerEventSourcingHandler(Class<?> messagePayloadClass, EventSourcingHandler eventSourcingHandler);

    void registerEventHandler(Object eventHandlerInstance);

    void scanPackage(String packageName);
}
