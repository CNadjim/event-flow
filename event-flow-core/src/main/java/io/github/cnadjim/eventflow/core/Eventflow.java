package io.github.cnadjim.eventflow.core;

import io.github.cnadjim.eventflow.core.api.RegisterHandler;
import io.github.cnadjim.eventflow.core.api.SendCommand;
import io.github.cnadjim.eventflow.core.api.SendEvent;
import io.github.cnadjim.eventflow.core.api.SendQuery;
import io.github.cnadjim.eventflow.core.service.*;
import io.github.cnadjim.eventflow.core.spi.*;
import io.github.cnadjim.eventflow.core.stub.InMemoryAggregateStore;
import io.github.cnadjim.eventflow.core.stub.InMemoryEventStore;
import io.github.cnadjim.eventflow.core.stub.InMemoryHandlerRegistry;
import io.github.cnadjim.eventflow.core.stub.StubEventBus;

import java.util.Optional;

import static java.util.Objects.isNull;

public record Eventflow(SendQuery queryGateway,
                        SendEvent eventGateway,
                        SendCommand commandGateway,
                        RegisterHandler handlerService) {


    public static final class EventFlowBuilder {
        EventStore eventStore;
        AggregateStore aggregateStore;
        EventPublisher eventPublisher;
        EventSubscriber eventSubscriber;
        HandlerRegistry handlerRegistry;


        public EventFlowBuilder aggregateStore(AggregateStore aggregateStore) {
            this.aggregateStore = aggregateStore;
            return this;
        }

        public EventFlowBuilder eventPublisher(EventPublisher eventPublisher) {
            this.eventPublisher = eventPublisher;
            return this;
        }

        public EventFlowBuilder eventStore(EventStore eventStore) {
            this.eventStore = eventStore;
            return this;
        }

        public EventFlowBuilder eventSubscriber(EventSubscriber eventSubscriber) {
            this.eventSubscriber = eventSubscriber;
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

            final SendQuery sendQuery = new QueryGateway(handlerRegistry);
            final SendEvent sendEvent = new EventGateway(handlerRegistry);

            if (isNull(eventPublisher) || isNull(eventSubscriber)) {
                final StubEventBus stubEventBus = new StubEventBus(sendEvent);
                eventPublisher = stubEventBus;
                eventSubscriber = stubEventBus;
            }

            final AggregateService aggregateService = new AggregateService(eventStore, aggregateStore, handlerRegistry);
            final SendCommand sendCommand = new CommandGateway(eventStore, eventPublisher, handlerRegistry, aggregateService);
            final RegisterHandler registerHandler = new HandlerService(eventSubscriber, handlerRegistry);

            return new Eventflow(
                    sendQuery,
                    sendEvent,
                    sendCommand,
                    registerHandler
            );
        }
    }
}
