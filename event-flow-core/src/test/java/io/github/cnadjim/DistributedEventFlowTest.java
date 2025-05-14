package io.github.cnadjim;

import io.github.cnadjim.customer.*;
import io.github.cnadjim.eventflow.core.Eventflow;
import io.github.cnadjim.eventflow.core.domain.response.ResponseType;
import io.github.cnadjim.eventflow.core.port.AggregateStore;
import io.github.cnadjim.eventflow.core.port.EventStore;
import io.github.cnadjim.eventflow.core.port.MessageBus;
import io.github.cnadjim.eventflow.core.stub.DefaultMessageBus;
import io.github.cnadjim.eventflow.core.stub.InMemoryAggregateStore;
import io.github.cnadjim.eventflow.core.stub.InMemoryEventStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DistributedEventFlowTest {
    private final String rootPackageName = getClass().getPackageName();

    private final MessageBus messageBus = new DefaultMessageBus();
    private final EventStore eventStore = new InMemoryEventStore();
    private final AggregateStore aggregateStore = new InMemoryAggregateStore();

    private final Eventflow eventflow1 = new Eventflow.EventFlowBuilder().messageBus(messageBus).eventStore(eventStore).aggregateStore(aggregateStore).build();
    private final Eventflow eventflow2 = new Eventflow.EventFlowBuilder().messageBus(messageBus).eventStore(eventStore).aggregateStore(aggregateStore).build();
    private final Eventflow eventflow3 = new Eventflow.EventFlowBuilder().messageBus(messageBus).eventStore(eventStore).aggregateStore(aggregateStore).build();

    @BeforeAll
    public static void setUp() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "DEBUG");
    }

    @Test
    public void command_result_should_succeed() {
        eventflow2.scanPackage().scan(rootPackageName).forEach(handler -> eventflow2.handlerService().register(handler));

        final String commandId = UUID.randomUUID().toString();
        final CustomerCommand command = new CustomerCommand.CreateCustomerCommand(commandId, "nadjim");
        final String commandResult = eventflow1.commandGateway().send(command).join();

        assertEquals(commandId, commandResult);
    }

    @Test
    public void query_result_should_succeed() {
        eventflow2.scanObject().scan(new CustomerQueryHandler()).forEach(handler -> eventflow2.handlerService().register(handler));

        final CustomerQuery.FindAllCustomer findAllCustomer = new CustomerQuery.FindAllCustomer();
        final Collection<CustomerEntity> customers = eventflow1.queryGateway().send(findAllCustomer, ResponseType.collectionOf(CustomerEntity.class)).join();

        assertEquals(1, customers.size());
    }


    @Test
    public void query_result_should_fail_with_conversion() {
        eventflow2.scanObject().scan(new CustomerQueryHandler()).forEach(handler -> eventflow2.handlerService().register(handler));

        assertThrows(CompletionException.class, () -> {
            final CustomerQuery.FindCustomerById findCustomerById = new CustomerQuery.FindCustomerById("toto");
            eventflow1.queryGateway().send(findCustomerById, ResponseType.instanceOf(CustomerAggregate.class)).join();
        });
    }

    @Test
    public void query_result_should_fail_with_handler() {
        eventflow2.scanObject().scan(new CustomerQueryHandler()).forEach(handler -> eventflow2.handlerService().register(handler));

        assertThrows(CompletionException.class, () -> {
            final CustomerQuery.ThrowAError throwAError = new CustomerQuery.ThrowAError();
            eventflow1.queryGateway().send(throwAError, ResponseType.instanceOf(CustomerAggregate.class)).join();
        });
    }

    @Test
    public void event_result_should_fail_with_handler() {
        eventflow2.scanPackage().scan(rootPackageName).forEach(handler -> eventflow2.handlerService().register(handler));

        final String commandId = UUID.randomUUID().toString();
        final CustomerCommand command = new CustomerCommand.CreateCustomerCommand(commandId, "nadjim");
        eventflow1.commandGateway().send(command);

        final CustomerEventHandler customerEventHandler = new CustomerEventHandler(null);
        eventflow3.scanObject().scan(customerEventHandler).forEach(handler -> eventflow3.handlerService().register(handler));

        assertThrows(CompletionException.class, () -> {
            final CustomerEvent.CustomerBirthdayUpdatedEvent customerBirthdayUpdatedEvent = new CustomerEvent.CustomerBirthdayUpdatedEvent(commandId, LocalDate.now());
            eventflow2.eventGateway().send(customerBirthdayUpdatedEvent).join();
        });
    }
}
