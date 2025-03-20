package io.github.cnadjim.evenflow.core;

import io.github.cnadjim.evenflow.core.domain.CustomerCommand;
import io.github.cnadjim.evenflow.core.domain.CustomerEvent;
import io.github.cnadjim.evenflow.core.domain.CustomerEventHandler;
import io.github.cnadjim.eventflow.core.Eventflow;
import io.github.cnadjim.eventflow.core.spi.AggregateStore;
import io.github.cnadjim.eventflow.core.spi.EventStore;
import io.github.cnadjim.eventflow.core.stub.InMemoryAggregateStore;
import io.github.cnadjim.eventflow.core.stub.InMemoryEventStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class EventFlowTest {

    private final String rootPackageName = getClass().getPackageName();
    private final EventStore eventStore = new InMemoryEventStore();
    private final AggregateStore aggregateStore = new InMemoryAggregateStore();
    private final Eventflow eventflow = new Eventflow.EventFlowBuilder().eventStore(eventStore).aggregateStore(aggregateStore).build();

    @Mock
    public CustomerEventHandler customerEventHandler;

    @BeforeAll
    public static void setUp() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "DEBUG");
    }


    public void event_flow_test() throws ExecutionException, InterruptedException {
        String aggregateId = UUID.randomUUID().toString();

        CustomerCommand.CreateCustomerCommand createCmd = new CustomerCommand.CreateCustomerCommand(aggregateId, "Alice");

        eventflow.handlerService().scanPackage(rootPackageName);
        eventflow.handlerService().scanInstance(customerEventHandler);

        CompletableFuture<String> createCommandResultAsCompletable = eventflow.commandGateway().sendCommand(createCmd);
        String createCommandResult = createCommandResultAsCompletable.get();

        Thread.sleep(200);

        assertEquals(aggregateId, createCommandResult);
        assertEquals(Optional.empty(), aggregateStore.findTopByAggregateIdOrderByVersionDesc(aggregateId));
        Mockito.verify(customerEventHandler, Mockito.times(1)).on(any(CustomerEvent.CustomerCreatedEvent.class));

        CustomerCommand.UpdateCustomerNameCommand updateCmd = new CustomerCommand.UpdateCustomerNameCommand(aggregateId, "Alica");

        CompletableFuture<String> updateCommandResultAsCompletable = eventflow.commandGateway().sendCommand(updateCmd);
        String updateCommandResult = updateCommandResultAsCompletable.get();

        Thread.sleep(200);

        assertEquals(aggregateId, updateCommandResult);
        assertEquals(Optional.empty(), aggregateStore.findTopByAggregateIdOrderByVersionDesc(aggregateId));
        Mockito.verify(customerEventHandler, Mockito.times(1)).on(any(CustomerEvent.CustomerNameUpdatedEvent.class));

        Collection<CompletableFuture<?>> updates = new ArrayList<>();

        IntStream.range(0, 9998).forEach(number -> {
            CustomerCommand.UpdateCustomerBirthdayCommand updateCustomerBirthdayCommand = new CustomerCommand.UpdateCustomerBirthdayCommand(aggregateId, LocalDate.of(number, 5, 17));
            updates.add(eventflow.commandGateway().sendCommand(updateCustomerBirthdayCommand));
        });

        CompletableFuture.allOf(updates.toArray(new CompletableFuture<?>[0])).get();

        int threshold = aggregateStore.findTopByAggregateIdOrderByVersionDesc(aggregateId).get().threshold();

        assertEquals(10000L, StreamSupport.stream((eventStore.findAllByAggregateIdOrderByTimestampAsc(aggregateId).spliterator()), false).count());
        assertEquals(9800L, StreamSupport.stream((eventStore.findAllByAggregateIdOrderByTimestampAsc(aggregateId).spliterator()), false).count() - threshold);
    }
}
