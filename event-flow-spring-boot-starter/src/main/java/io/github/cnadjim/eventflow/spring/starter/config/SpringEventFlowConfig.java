package io.github.cnadjim.eventflow.spring.starter.config;

import io.github.cnadjim.eventflow.core.api.SendCommand;
import io.github.cnadjim.eventflow.core.api.SendEvent;
import io.github.cnadjim.eventflow.core.api.SendQuery;
import io.github.cnadjim.eventflow.core.service.AggregateService;
import io.github.cnadjim.eventflow.core.service.HandlerService;
import io.github.cnadjim.eventflow.core.service.TopicService;
import io.github.cnadjim.eventflow.core.service.dispatcher.CommandDispatcher;
import io.github.cnadjim.eventflow.core.service.dispatcher.EventDispatcher;
import io.github.cnadjim.eventflow.core.service.dispatcher.QueryDispatcher;
import io.github.cnadjim.eventflow.core.service.gateway.CommandGateway;
import io.github.cnadjim.eventflow.core.service.gateway.EventGateway;
import io.github.cnadjim.eventflow.core.service.gateway.QueryGateway;
import io.github.cnadjim.eventflow.core.spi.*;
import io.github.cnadjim.eventflow.core.stub.*;
import io.github.cnadjim.eventflow.spring.starter.exception.SpringErrorConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SpringEventFlowConfig {

    @Bean
    @ConditionalOnMissingBean(value = MessageBus.class)
    public MessageBus messageBus() {
        return new DefaultMessageBus();
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
    @ConditionalOnMissingBean(HandlerRegistry.class)
    public HandlerRegistry handlerRegistry() {
        return new InMemoryHandlerRegistry();
    }

    @Bean
    @ConditionalOnMissingBean(TopicRegistry.class)
    public TopicRegistry topicRegistry() {
        return new InMemoryTopicRegistry();
    }


    @Bean
    @Primary
    public TopicService topicService(TopicRegistry topicRegistry) {
        return new TopicService(topicRegistry);
    }

    @Bean
    @Primary
    public ErrorConverter errorConverter() {
        return new SpringErrorConverter();
    }

    @Bean
    @Primary
    public AggregateService aggregateService(final EventStore eventStore,
                                             final AggregateStore aggregateStore,
                                             final HandlerRegistry handlerRegistry) {
        return new AggregateService(eventStore, aggregateStore, handlerRegistry);
    }


    @Bean
    @Primary
    public EventDispatcher eventDispatcher(final ErrorConverter errorConverter,
                                           final MessageBus messageBus,
                                           final HandlerRegistry handlerRegistry) {
        return new EventDispatcher(messageBus, errorConverter, handlerRegistry);
    }

    @Bean
    @Primary
    public QueryDispatcher queryDispatcher(final ErrorConverter errorConverter, final MessageBus messageBus,
                                           final HandlerRegistry handlerRegistry) {
        return new QueryDispatcher(messageBus, errorConverter, handlerRegistry);
    }

    @Bean
    @Primary
    public CommandDispatcher commandDispatcher(final ErrorConverter errorConverter, final MessageBus messageBus,
                                               final EventStore eventStore,
                                               final HandlerRegistry handlerRegistry,
                                               final AggregateService aggregateService) {
        return new CommandDispatcher(messageBus, eventStore, errorConverter, handlerRegistry, aggregateService);
    }

    @Bean

    @Primary
    public HandlerService handlerService(final TopicService topicService,
                                         final HandlerRegistry handlerRegistry,
                                         final EventDispatcher eventDispatcher,
                                         final QueryDispatcher queryDispatcher,
                                         final CommandDispatcher commandDispatcher) {
        return new HandlerService(topicService, handlerRegistry, eventDispatcher, queryDispatcher, commandDispatcher);
    }

    @Bean
    @Primary
    public SendCommand sendCommand(final MessageBus messageBus) {
        return new CommandGateway(messageBus);
    }

    @Bean
    @Primary
    public SendEvent sendEvent(final MessageBus messageBus) {
        return new EventGateway(messageBus);
    }

    @Bean
    @Primary
    public SendQuery sendQuery(final MessageBus messageBus) {
        return new QueryGateway(messageBus);
    }

}
