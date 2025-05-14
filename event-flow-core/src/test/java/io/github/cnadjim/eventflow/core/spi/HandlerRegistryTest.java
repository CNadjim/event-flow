package io.github.cnadjim.eventflow.core.spi;

import io.github.cnadjim.eventflow.core.domain.exception.HandlerNotFoundException;
import io.github.cnadjim.eventflow.core.port.HandlerRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;


@ExtendWith(MockitoExtension.class)
public class HandlerRegistryTest {

    @Spy
    private HandlerRegistry handlerRegistry;

    @Test
    void getEventHandler_should_throw_handler_not_found_exception() {
        doReturn(Optional.empty()).when(handlerRegistry).findEventHandler(nullable(Class.class));
        assertThrows(HandlerNotFoundException.class, () -> handlerRegistry.getEventHandler(String.class));
    }

    @Test
    void getQueryHandler_should_throw_handler_not_found_exception() {
        doReturn(Optional.empty()).when(handlerRegistry).findQueryHandler(nullable(Class.class));
        assertThrows(HandlerNotFoundException.class, () -> handlerRegistry.getQueryHandler(String.class));
    }

    @Test
    void getCommandHandler_should_throw_handler_not_found_exception() {
        doReturn(Optional.empty()).when(handlerRegistry).findCommandHandler(nullable(Class.class));
        assertThrows(HandlerNotFoundException.class, () -> handlerRegistry.getCommandHandler(String.class));
    }

    @Test
    void getEventSourcingHandler_should_throw_handler_not_found_exception() {
        doReturn(Optional.empty()).when(handlerRegistry).findEventSourcingHandler(nullable(Class.class));
        assertThrows(HandlerNotFoundException.class, () -> handlerRegistry.getEventSourcingHandler(String.class));
    }
}
