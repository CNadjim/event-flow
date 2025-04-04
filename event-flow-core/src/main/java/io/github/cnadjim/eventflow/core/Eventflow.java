package io.github.cnadjim.eventflow.core;

import io.github.cnadjim.eventflow.core.api.*;
import io.github.cnadjim.eventflow.core.service.AggregateService;
import io.github.cnadjim.eventflow.core.service.HandlerService;
import io.github.cnadjim.eventflow.core.service.dispatcher.CommandDispatcher;
import io.github.cnadjim.eventflow.core.service.dispatcher.EventDispatcher;
import io.github.cnadjim.eventflow.core.service.dispatcher.QueryDispatcher;
import io.github.cnadjim.eventflow.core.service.gateway.CommandGateway;
import io.github.cnadjim.eventflow.core.service.gateway.EventGateway;
import io.github.cnadjim.eventflow.core.service.gateway.QueryGateway;
import io.github.cnadjim.eventflow.core.spi.*;
import io.github.cnadjim.eventflow.core.stub.*;

import java.util.Optional;

public record Eventflow(SendQuery queryGateway,
                        SendEvent eventGateway,
                        SendCommand commandGateway,
                        RegisterHandler handlerService,
                        ScanObject scanObject,
                        ScanPackage scanPackage) {

    public static final class EventFlowBuilder {
        EventStore eventStore;
        MessageBus messageBus;
        ErrorConverter errorConverter;
        AggregateStore aggregateStore;
        HandlerRegistry handlerRegistry;

        public EventFlowBuilder errorConverter(ErrorConverter errorConverter) {
            this.errorConverter = errorConverter;
            return this;
        }

        public EventFlowBuilder aggregateStore(AggregateStore aggregateStore) {
            this.aggregateStore = aggregateStore;
            return this;
        }

        public EventFlowBuilder messageBus(MessageBus messageBus) {
            this.messageBus = messageBus;
            return this;
        }

        public EventFlowBuilder eventStore(EventStore eventStore) {
            this.eventStore = eventStore;
            return this;
        }


        public EventFlowBuilder handlerRegistry(HandlerRegistry handlerRegistry) {
            this.handlerRegistry = handlerRegistry;
            return this;
        }


        public Eventflow build() {

            eventStore = Optional.ofNullable(eventStore).orElse(new InMemoryEventStore());
            handlerRegistry = Optional.ofNullable(handlerRegistry).orElse(new InMemoryHandlerRegistry());
            aggregateStore = Optional.ofNullable(aggregateStore).orElse(new InMemoryAggregateStore());
            messageBus = Optional.ofNullable(messageBus).orElse(new DefaultMessageBus());
            errorConverter = Optional.ofNullable(errorConverter).orElse(new DefaultErrorConverter());

            final QueryGateway queryGateway = new QueryGateway(messageBus);
            final EventGateway eventGateway = new EventGateway(messageBus);
            final CommandGateway commandGateway = new CommandGateway(messageBus);

            final AggregateService aggregateService = new AggregateService(eventStore, aggregateStore, handlerRegistry);
            final EventDispatcher eventDispatcher = new EventDispatcher(errorConverter, messageBus, handlerRegistry);
            final QueryDispatcher queryDispatcher = new QueryDispatcher(errorConverter, messageBus, handlerRegistry);
            final CommandDispatcher commandDispatcher = new CommandDispatcher(errorConverter, messageBus, eventStore, handlerRegistry, aggregateService);

            final HandlerService handlerService = new HandlerService(handlerRegistry, eventDispatcher, queryDispatcher, commandDispatcher);

            return new Eventflow(
                    queryGateway,
                    eventGateway,
                    commandGateway,
                    handlerService,
                    handlerService,
                    handlerService
            );
        }
    }
}
