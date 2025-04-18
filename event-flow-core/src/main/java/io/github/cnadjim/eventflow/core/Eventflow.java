package io.github.cnadjim.eventflow.core;

import io.github.cnadjim.eventflow.core.api.*;
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

import java.util.Optional;

/**
 * Main entry point for the Event Flow framework.
 * This record encapsulates all the core components needed for event-driven architecture.
 * It provides access to gateways for sending commands, queries, and events,
 * as well as services for registering handlers and managing topics.
 *
 * @param scanObject     Interface for scanning and registering individual objects
 * @param scanPackage    Interface for scanning and registering packages
 * @param queryGateway   Gateway for sending queries
 * @param eventGateway   Gateway for sending events
 * @param topicService   Service for managing topics
 * @param commandGateway Gateway for sending commands
 * @param handlerService Service for registering handlers
 */
public record Eventflow(ScanObject scanObject,
                        ScanPackage scanPackage,
                        SendQuery queryGateway,
                        SendEvent eventGateway,
                        TopicService topicService,
                        SendCommand commandGateway,
                        RegisterHandler handlerService) {

    /**
     * Builder class for creating Eventflow instances.
     * Provides a fluent API for configuring the components of the Eventflow system.
     * If components are not explicitly provided, default in-memory implementations will be used.
     */
    public static final class EventFlowBuilder {
        /** Store for persisting events */
        EventStore eventStore;
        /** Bus for sending messages between components */
        MessageBus messageBus;
        /** Registry for managing topics */
        TopicRegistry topicRegistry;
        /** Converter for handling errors */
        ErrorConverter errorConverter;
        /** Store for persisting aggregates */
        AggregateStore aggregateStore;
        /** Registry for managing handlers */
        HandlerRegistry handlerRegistry;

        /**
         * Sets the error converter to be used by the Eventflow system.
         *
         * @param errorConverter The error converter implementation
         * @return This builder instance for method chaining
         */
        public EventFlowBuilder errorConverter(ErrorConverter errorConverter) {
            this.errorConverter = errorConverter;
            return this;
        }

        /**
         * Sets the topic registry to be used by the Eventflow system.
         *
         * @param topicRegistry The topic registry implementation
         * @return This builder instance for method chaining
         */
        public EventFlowBuilder topicRegistry(TopicRegistry topicRegistry) {
            this.topicRegistry = topicRegistry;
            return this;
        }

        /**
         * Sets the aggregate store to be used by the Eventflow system.
         *
         * @param aggregateStore The aggregate store implementation
         * @return This builder instance for method chaining
         */
        public EventFlowBuilder aggregateStore(AggregateStore aggregateStore) {
            this.aggregateStore = aggregateStore;
            return this;
        }

        /**
         * Sets the message bus to be used by the Eventflow system.
         *
         * @param messageBus The message bus implementation
         * @return This builder instance for method chaining
         */
        public EventFlowBuilder messageBus(MessageBus messageBus) {
            this.messageBus = messageBus;
            return this;
        }

        /**
         * Sets the event store to be used by the Eventflow system.
         *
         * @param eventStore The event store implementation
         * @return This builder instance for method chaining
         */
        public EventFlowBuilder eventStore(EventStore eventStore) {
            this.eventStore = eventStore;
            return this;
        }

        /**
         * Sets the handler registry to be used by the Eventflow system.
         *
         * @param handlerRegistry The handler registry implementation
         * @return This builder instance for method chaining
         */
        public EventFlowBuilder handlerRegistry(HandlerRegistry handlerRegistry) {
            this.handlerRegistry = handlerRegistry;
            return this;
        }

        /**
         * Builds and returns a configured Eventflow instance.
         * Any components not explicitly set will use default in-memory implementations.
         *
         * @return A fully configured Eventflow instance
         */
        public Eventflow build() {

            eventStore = Optional.ofNullable(eventStore).orElse(new InMemoryEventStore());
            handlerRegistry = Optional.ofNullable(handlerRegistry).orElse(new InMemoryHandlerRegistry());
            aggregateStore = Optional.ofNullable(aggregateStore).orElse(new InMemoryAggregateStore());
            messageBus = Optional.ofNullable(messageBus).orElse(new DefaultMessageBus());
            errorConverter = Optional.ofNullable(errorConverter).orElse(new DefaultErrorConverter());
            topicRegistry = Optional.ofNullable(topicRegistry).orElse(new InMemoryTopicRegistry());

            final QueryGateway queryGateway = new QueryGateway(messageBus);
            final EventGateway eventGateway = new EventGateway(messageBus);
            final CommandGateway commandGateway = new CommandGateway(messageBus);

            final TopicService topicService = new TopicService(topicRegistry);

            final AggregateService aggregateService = new AggregateService(eventStore, aggregateStore, handlerRegistry);
            final EventDispatcher eventDispatcher = new EventDispatcher(messageBus, errorConverter, handlerRegistry);
            final QueryDispatcher queryDispatcher = new QueryDispatcher(messageBus, errorConverter, handlerRegistry);
            final CommandDispatcher commandDispatcher = new CommandDispatcher(messageBus, eventStore, errorConverter, handlerRegistry, aggregateService);

            final HandlerService handlerService = new HandlerService(topicService, handlerRegistry, eventDispatcher, queryDispatcher, commandDispatcher);

            return new Eventflow(
                    handlerService,
                    handlerService,
                    queryGateway,
                    eventGateway,
                    topicService,
                    commandGateway,
                    handlerService
            );
        }
    }
}
