package io.github.cnadjim.evenflow.core;

import io.github.cnadjim.eventflow.core.Eventflow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import io.github.cnadjim.evenflow.core.domain.CustomerCommand;
import io.github.cnadjim.evenflow.core.domain.CustomerEvent;
import io.github.cnadjim.evenflow.core.domain.CustomerEventHandler;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class EventFlowTest {

    private final String rootPackageName = getClass().getPackageName();
    private final Eventflow eventflow = new Eventflow.EventFlowBuilder().build();

    @Mock
    public CustomerEventHandler customerEventHandler;

    @Test
    public void event_flow_test() {
        String aggregateId = UUID.randomUUID().toString();

        CustomerCommand.CreateCustomerCommand createCmd = new CustomerCommand.CreateCustomerCommand(aggregateId, "Alice");

        eventflow.handlerService().scanPackage(rootPackageName);
        eventflow.handlerService().registerEventHandler(customerEventHandler);

        CompletableFuture<String> createCommandResultAsCompletable = eventflow.commandGateway().sendCommand(createCmd);
        String createCommandResult = createCommandResultAsCompletable.join();

        assertEquals(aggregateId, createCommandResult);

        CustomerCommand.UpdateCustomerNameCommand updateCmd = new CustomerCommand.UpdateCustomerNameCommand(aggregateId, "Alica");

        CompletableFuture<String> updateCommandResultAsCompletable = eventflow.commandGateway().sendCommand(updateCmd);
        String updateCommandResult = updateCommandResultAsCompletable.join();

        assertEquals(aggregateId, updateCommandResult);

        Mockito.verify(customerEventHandler, Mockito.times(1)).on(any(CustomerEvent.CustomerCreatedEvent.class));
        Mockito.verify(customerEventHandler, Mockito.times(1)).on(any(CustomerEvent.CustomerNameUpdatedEvent.class));
    }
}
