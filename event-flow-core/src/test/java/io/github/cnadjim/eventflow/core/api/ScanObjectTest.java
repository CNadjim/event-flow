package io.github.cnadjim.eventflow.core.api;

import io.github.cnadjim.eventflow.core.domain.handler.CommandHandler;
import io.github.cnadjim.eventflow.core.domain.handler.EventHandler;
import io.github.cnadjim.eventflow.core.domain.handler.Handler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ScanObjectTest {

    private final ScanObject scanObject = instanceToScan -> List.of(mock(EventHandler.class), mock(CommandHandler.class));

    @Test
    void scan_with_handlerClass_should_filter_handlers_correctly() {
        Object instance = new Object();
        Collection<Handler> handlers = scanObject.scan(instance, CommandHandler.class);
        assertEquals(1, handlers.size());
    }

    @Test
    void scan_with_handlerClasses_should_filter_handlers_correctly() {
        Object instance = new Object();
        Collection<Class<? extends Handler>> handlerClasses = List.of(EventHandler.class);
        Collection<Handler> handlers = scanObject.scan(instance, handlerClasses);
        assertEquals(1, handlers.size());
    }
}
