package io.github.cnadjim.eventflow.core.service.dispatcher;

import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.domain.MessageResult;
import io.github.cnadjim.eventflow.core.domain.Query;
import io.github.cnadjim.eventflow.core.domain.flux.MessageDispatcher;
import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.handler.QueryHandler;
import io.github.cnadjim.eventflow.core.spi.ErrorConverter;
import io.github.cnadjim.eventflow.core.spi.HandlerRegistry;
import io.github.cnadjim.eventflow.core.spi.MessageBus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DomainService
public class QueryDispatcher implements MessageDispatcher<Query> {

    private final ErrorConverter errorConverter;
    private final MessageBus messageBus;
    private final HandlerRegistry handlerRegistry;

    public QueryDispatcher(ErrorConverter errorConverter, MessageBus messageBus,
                           HandlerRegistry handlerRegistry) {
        this.errorConverter = errorConverter;
        this.messageBus = messageBus;
        this.handlerRegistry = handlerRegistry;
    }

    @Override
    public Class<Query> classOfMessage() {
        return Query.class;
    }

    @Override
    public void dispatch(Query message) {
        try {
            final QueryHandler queryHandler = handlerRegistry.getQueryHandler(message.payloadClass());
            final Object result = queryHandler.handle(message);
            messageBus.publish(MessageResult.success(message, result));
        } catch (Exception exception) {
            messageBus.publish(MessageResult.failure(message, exception, errorConverter));
        }
    }

    @Override
    public void subscribe(MessageSubscriber<Query> subscriber) {
        messageBus.subscribe(subscriber);
    }
}
