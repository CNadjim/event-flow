package io.github.cnadjim;

import io.github.cnadjim.customer.CustomerCommand;
import io.github.cnadjim.eventflow.core.Eventflow;
import io.github.cnadjim.eventflow.core.domain.exception.EventFlowException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.CompletionException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventFlowExceptionTest {
    private final String rootPackageName = getClass().getPackageName();
    private final Eventflow eventflow = new Eventflow.EventFlowBuilder().build();

    @Test
    public void should_convert_to_event_flow_exception() {
        eventflow.scanPackage().scan(rootPackageName).forEach(handler -> eventflow.handlerService().register(handler));
        final String commandId = UUID.randomUUID().toString();
        final CustomerCommand command = new CustomerCommand.CreateCustomerCommand(commandId, null);
        final String commandResult = eventflow.commandGateway().send(command).join();
        assertEquals(commandId, commandResult);
        final CompletionException exception = assertThrows(CompletionException.class, () -> eventflow.commandGateway().send(command).join());
        final Throwable cause = exception.getCause();
        assertThat(cause).isInstanceOf(EventFlowException.class);
        final EventFlowException eventFlowException = (EventFlowException) cause;
        assertThat(eventFlowException.message()).isEqualTo("Customer already exists for id " + commandId);
    }
}
