package io.github.cnadjim.eventflow.spring.starter.config;

import io.github.cnadjim.eventflow.core.api.RegisterHandler;
import io.github.cnadjim.eventflow.core.api.SendCommand;
import io.github.cnadjim.eventflow.core.api.SendEvent;
import io.github.cnadjim.eventflow.core.api.SendQuery;
import io.github.cnadjim.eventflow.core.service.CommandGateway;
import io.github.cnadjim.eventflow.core.service.EventGateway;
import io.github.cnadjim.eventflow.core.service.HandlerService;
import io.github.cnadjim.eventflow.core.service.QueryGateway;
import io.github.cnadjim.eventflow.core.spi.*;
import io.github.cnadjim.eventflow.core.stub.InMemoryAggregateStore;
import io.github.cnadjim.eventflow.core.stub.InMemoryEventStore;
import io.github.cnadjim.eventflow.core.stub.InMemoryHandlerRegistry;
import io.github.cnadjim.eventflow.core.stub.StubEventBus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

public class EventFlowConfig {

    @Bean
    @ConditionalOnMissingBean(value = {EventSubscriber.class, EventPublisher.class})
    public StubEventBus stubEventBus(final SendEvent sendEvent) {
        return new StubEventBus(sendEvent);
    }

    @Bean
    @ConditionalOnMissingBean(EventStore.class)
    public AggregateStore aggregateStore() {
        return new InMemoryAggregateStore();
    }

    @Bean
    @ConditionalOnMissingBean(EventStore.class)
    public EventStore eventStore() {
        return new InMemoryEventStore();
    }

    @Bean
    @Primary
    public HandlerRegistry handlerRegistry() {
        return new InMemoryHandlerRegistry();
    }

    @Bean
    @Primary
    public RegisterHandler registerHandler(final EventSubscriber eventSubscriber, final HandlerRegistry handlerRegistry) {
        return new HandlerService(eventSubscriber, handlerRegistry);
    }

    @Bean
    @Primary
    public SendCommand sendCommand(final EventStore eventStore,
                                   final EventPublisher eventPublisher,
                                   final AggregateStore aggregateStore,
                                   final HandlerRegistry handlerRegistry) {
        return new CommandGateway(eventStore,aggregateStore, eventPublisher, handlerRegistry);
    }

    @Bean
    @Primary
    public SendEvent sendEvent(final HandlerRegistry handlerRegistry) {
        return new EventGateway(handlerRegistry);
    }

    @Bean
    @Primary
    public SendQuery sendQuery(final HandlerRegistry handlerRegistry) {
        return new QueryGateway(handlerRegistry);
    }

}
