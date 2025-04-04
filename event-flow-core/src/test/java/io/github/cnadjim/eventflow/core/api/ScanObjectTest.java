package io.github.cnadjim.eventflow.core.api;

import io.github.cnadjim.eventflow.core.domain.handler.CommandHandler;
import io.github.cnadjim.eventflow.core.domain.handler.Handler;
import io.github.cnadjim.eventflow.core.domain.handler.QueryHandler;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class ScanObjectTest {

    @Spy
    private ScanObject scanObject;

    
    void scan() {
        final Collection<Handler> handlers = Collections.emptyList();
        doReturn(handlers).when(scanObject).scan(nullable(Object.class));

        Collection<Handler> scanEventResult = scanObject.scan(null, QueryHandler.class);

        Collection<Handler> scanResults = scanObject.scan(null, Lists.newArrayList(QueryHandler.class, CommandHandler.class));

        assertEquals(handlers, scanEventResult);
        assertEquals(handlers, scanResults);
    }
}
