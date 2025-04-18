package io.github.cnadjim.eventflow.core.service.dispatcher;

import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.domain.flux.MessageDispatcher;
import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.handler.QueryHandler;
import io.github.cnadjim.eventflow.core.domain.message.Query;
import io.github.cnadjim.eventflow.core.domain.message.QueryResult;
import io.github.cnadjim.eventflow.core.spi.ErrorConverter;
import io.github.cnadjim.eventflow.core.spi.HandlerRegistry;
import io.github.cnadjim.eventflow.core.spi.MessageBus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DomainService
public class QueryDispatcher implements MessageDispatcher<Query> {

    private final MessageBus messageBus;
    private final ErrorConverter errorConverter;
    private final HandlerRegistry handlerRegistry;

    public QueryDispatcher(MessageBus messageBus,
                           ErrorConverter errorConverter,
                           HandlerRegistry handlerRegistry) {
        this.errorConverter = errorConverter;
        this.messageBus = messageBus;
        this.handlerRegistry = handlerRegistry;
    }

    @Override
    public Class<Query> dispatchMessageType() {
        return Query.class;
    }

    @Override
    public void dispatch(Query message) {
        try {
            log.debug("[ {} ] Dispatching query {}", message.id(), message.payloadClassSimpleName());
            final QueryHandler queryHandler = handlerRegistry.getQueryHandler(message.payloadClass());
            final Object result = queryHandler.handle(message);
            messageBus.publish(QueryResult.success(message, result));
            log.debug("[ {} ] Dispatching query finished successfully", message.id());
        } catch (Exception exception) {
            messageBus.publish(QueryResult.failure(message, errorConverter.convert(exception)));
            log.error("[ {} ] Dispatching query finished with error", message.id(), exception);
        }
    }

    @Override
    public void subscribe(MessageSubscriber<Query> subscriber) {
        messageBus.subscribe(subscriber);
    }
}
