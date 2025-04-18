package io.github.cnadjim.eventflow.core.service;

import io.github.cnadjim.eventflow.core.domain.handler.EventSourcingHandler;
import io.github.cnadjim.eventflow.core.domain.message.Aggregate;
import io.github.cnadjim.eventflow.core.domain.message.Event;
import io.github.cnadjim.eventflow.core.spi.AggregateStore;
import io.github.cnadjim.eventflow.core.spi.EventStore;
import io.github.cnadjim.eventflow.core.spi.HandlerRegistry;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AggregateServiceTest {

    @Mock
    private EventStore eventStore;

    @Mock
    private AggregateStore aggregateStore;

    @Mock
    private HandlerRegistry handlerRegistry;

    @Mock
    private EventSourcingHandler eventSourcingHandler;

    private AggregateService aggregateService;

    @BeforeEach
    void setUp() {
        aggregateService = new AggregateService(eventStore, aggregateStore, handlerRegistry);
    }

    @Test
    void loadAggregateState_WithEvents_ShouldApplyEventsToAggregate() {
        // Arrange
        String aggregateId = "test-aggregate-id";
        TestPayload initialPayload = new TestPayload(aggregateId, "initial");
        TestPayload updatedPayload = new TestPayload(aggregateId, "updated");

        io.github.cnadjim.eventflow.core.domain.message.Aggregate initialAggregate = 
            new io.github.cnadjim.eventflow.core.domain.message.Aggregate(0L, initialPayload, aggregateId);
        io.github.cnadjim.eventflow.core.domain.message.Aggregate updatedAggregate = 
            new io.github.cnadjim.eventflow.core.domain.message.Aggregate(1L, updatedPayload, aggregateId);

        Event event = new Event("event-id", new TestEvent("update"), Instant.now(), aggregateId);

        when(aggregateStore.findTopByAggregateIdOrderByVersionDesc(aggregateId))
            .thenReturn(Optional.of(initialAggregate));
        when(handlerRegistry.getEventSourcingHandler(event.payloadClass()))
            .thenReturn(eventSourcingHandler);
        when(eventSourcingHandler.apply(event, initialAggregate))
            .thenReturn(updatedAggregate);

        // Act
        io.github.cnadjim.eventflow.core.domain.message.Aggregate result = 
            aggregateService.loadAggregateState(aggregateId, List.of(event));

        // Assert
        assertEquals(updatedAggregate, result);
        verify(handlerRegistry).getEventSourcingHandler(event.payloadClass());
        verify(eventSourcingHandler).apply(event, initialAggregate);
        verify(aggregateStore, never()).save(any());
    }

    @Test
    void loadAggregateState_WithNoExistingAggregate_ShouldCreateNewAggregate() {
        // Arrange
        String aggregateId = "test-aggregate-id";
        TestPayload payload = new TestPayload(aggregateId, "new");

        io.github.cnadjim.eventflow.core.domain.message.Aggregate newAggregate = 
            new io.github.cnadjim.eventflow.core.domain.message.Aggregate(1L, payload, aggregateId);

        Event event = new Event("event-id", new TestEvent("create"), Instant.now(), aggregateId);

        when(aggregateStore.findTopByAggregateIdOrderByVersionDesc(aggregateId))
            .thenReturn(Optional.empty());
        when(handlerRegistry.getEventSourcingHandler(event.payloadClass()))
            .thenReturn(eventSourcingHandler);
        when(eventSourcingHandler.apply(eq(event), any(io.github.cnadjim.eventflow.core.domain.message.Aggregate.class)))
            .thenReturn(newAggregate);

        // Act
        io.github.cnadjim.eventflow.core.domain.message.Aggregate result = 
            aggregateService.loadAggregateState(aggregateId, List.of(event));

        // Assert
        assertEquals(newAggregate, result);
        verify(handlerRegistry).getEventSourcingHandler(event.payloadClass());
        verify(eventSourcingHandler).apply(eq(event), any(io.github.cnadjim.eventflow.core.domain.message.Aggregate.class));
        verify(aggregateStore, never()).save(any());
    }

    @Test
    void loadAggregateState_WithNullPayload_ShouldDeleteAggregate() {
        // Arrange
        String aggregateId = "test-aggregate-id";
        TestPayload initialPayload = new TestPayload(aggregateId, "initial");

        io.github.cnadjim.eventflow.core.domain.message.Aggregate initialAggregate = 
            new io.github.cnadjim.eventflow.core.domain.message.Aggregate(0L, initialPayload, aggregateId);
        io.github.cnadjim.eventflow.core.domain.message.Aggregate nullPayloadAggregate = 
            new io.github.cnadjim.eventflow.core.domain.message.Aggregate(1L, null, aggregateId);

        Event event = new Event("event-id", new TestEvent("delete"), Instant.now(), aggregateId);

        when(aggregateStore.findTopByAggregateIdOrderByVersionDesc(aggregateId))
            .thenReturn(Optional.of(initialAggregate));
        when(handlerRegistry.getEventSourcingHandler(event.payloadClass()))
            .thenReturn(eventSourcingHandler);
        when(eventSourcingHandler.apply(event, initialAggregate))
            .thenReturn(nullPayloadAggregate);

        // Act
        io.github.cnadjim.eventflow.core.domain.message.Aggregate result = 
            aggregateService.loadAggregateState(aggregateId, List.of(event));

        // Assert
        assertEquals(nullPayloadAggregate, result);
        verify(eventStore).deleteAllByAggregateId(aggregateId);
        verify(aggregateStore).deleteAllByAggregateId(aggregateId);
    }

    @Test
    void loadAggregateState_WithSnapshotEnabled_ShouldSaveAggregateWhenThresholdReached() {
        // Arrange
        String aggregateId = "test-aggregate-id";
        TestPayload initialPayload = new TestPayload(aggregateId, "initial");
        TestPayload updatedPayload = new TestPayload(aggregateId, "updated");

        Aggregate initialAggregate = 
            new Aggregate(9L, initialPayload, aggregateId);
        Aggregate updatedAggregate = 
            new Aggregate(10L, updatedPayload, aggregateId);

        // Spy on the aggregates to mock snapshot behavior
        Aggregate initialAggregateSpy = Mockito.spy(initialAggregate);
        Aggregate updatedAggregateSpy = Mockito.spy(updatedAggregate);

        Mockito.when(initialAggregateSpy.isSnapshotEnabled()).thenReturn(true);
        Mockito.when(initialAggregateSpy.threshold()).thenReturn(5);
        Mockito.when(updatedAggregateSpy.isSnapshotEnabled()).thenReturn(true);
        Mockito.when(updatedAggregateSpy.threshold()).thenReturn(5);

        Event event = new Event("event-id", new TestEvent("update"), Instant.now(), aggregateId);

        when(aggregateStore.findTopByAggregateIdOrderByVersionDesc(aggregateId))
            .thenReturn(Optional.of(initialAggregateSpy));
        when(handlerRegistry.getEventSourcingHandler(event.payloadClass()))
            .thenReturn(eventSourcingHandler);
        when(eventSourcingHandler.apply(event, initialAggregateSpy))
            .thenReturn(updatedAggregateSpy);

        // Act
        Aggregate result = 
            aggregateService.loadAggregateState(aggregateId, List.of(event));

        // Assert
        assertEquals(updatedAggregateSpy, result);
        verify(aggregateStore).save(updatedAggregateSpy);
    }

    @Test
    void loadAggregateState_WithSnapshotEnabled_ShouldNotSaveAggregateWhenThresholdNotReached() {
        // Arrange
        String aggregateId = "test-aggregate-id";
        TestPayload initialPayload = new TestPayload(aggregateId, "initial");
        TestPayload updatedPayload = new TestPayload(aggregateId, "updated");

        Aggregate initialAggregate = 
            new Aggregate(8L, initialPayload, aggregateId);
        Aggregate updatedAggregate = 
            new Aggregate(9L, updatedPayload, aggregateId);

        // Spy on the aggregates to mock snapshot behavior
        Aggregate initialAggregateSpy = Mockito.spy(initialAggregate);
        Aggregate updatedAggregateSpy = Mockito.spy(updatedAggregate);

        Mockito.when(initialAggregateSpy.isSnapshotEnabled()).thenReturn(true);
        Mockito.when(initialAggregateSpy.threshold()).thenReturn(5);
        Mockito.when(updatedAggregateSpy.isSnapshotEnabled()).thenReturn(true);
        Mockito.when(updatedAggregateSpy.threshold()).thenReturn(5);

        Event event = new Event("event-id", new TestEvent("update"), Instant.now(), aggregateId);

        when(aggregateStore.findTopByAggregateIdOrderByVersionDesc(aggregateId))
            .thenReturn(Optional.of(initialAggregateSpy));
        when(handlerRegistry.getEventSourcingHandler(event.payloadClass()))
            .thenReturn(eventSourcingHandler);
        when(eventSourcingHandler.apply(event, initialAggregateSpy))
            .thenReturn(updatedAggregateSpy);

        // Act
        Aggregate result = 
            aggregateService.loadAggregateState(aggregateId, List.of(event));

        // Assert
        assertEquals(updatedAggregateSpy, result);
        verify(aggregateStore, never()).save(any());
    }

    @Test
    void loadAggregateState_WithNoEvents_ShouldReturnOriginalAggregate() {
        // Arrange
        String aggregateId = "test-aggregate-id";
        TestPayload payload = new TestPayload(aggregateId, "initial");

        io.github.cnadjim.eventflow.core.domain.message.Aggregate aggregate = 
            new io.github.cnadjim.eventflow.core.domain.message.Aggregate(0L, payload, aggregateId);

        when(aggregateStore.findTopByAggregateIdOrderByVersionDesc(aggregateId))
            .thenReturn(Optional.of(aggregate));

        // Act
        io.github.cnadjim.eventflow.core.domain.message.Aggregate result = 
            aggregateService.loadAggregateState(aggregateId, Collections.emptyList());

        // Assert
        assertEquals(aggregate, result);
        verify(handlerRegistry, never()).getEventSourcingHandler(any());
        verify(eventSourcingHandler, never()).apply(any(), any());
        verify(aggregateStore, never()).save(any());
    }

    @Test
    void loadPreviousAggregateState_WithSnapshotEnabled_ShouldLoadEventsStartingFromSnapshot() {
        // Arrange
        String aggregateId = "test-aggregate-id";
        TestPayload initialPayload = new TestPayload(aggregateId, "initial");
        TestPayload updatedPayload = new TestPayload(aggregateId, "updated");

        Aggregate initialAggregate = 
            new Aggregate(5L, initialPayload, aggregateId);
        Aggregate updatedAggregate = 
            new Aggregate(6L, updatedPayload, aggregateId);

        // Spy on the aggregates to mock snapshot behavior
        Aggregate initialAggregateSpy = Mockito.spy(initialAggregate);
        Aggregate updatedAggregateSpy = Mockito.spy(updatedAggregate);

        Mockito.when(initialAggregateSpy.isSnapshotEnabled()).thenReturn(true);
        Mockito.when(initialAggregateSpy.threshold()).thenReturn(5);
        Mockito.when(updatedAggregateSpy.isSnapshotEnabled()).thenReturn(true);
        Mockito.when(updatedAggregateSpy.threshold()).thenReturn(5);

        Event event = new Event("event-id", new TestEvent("update"), Instant.now(), aggregateId);

        when(aggregateStore.findTopByAggregateIdOrderByVersionDesc(aggregateId))
            .thenReturn(Optional.of(initialAggregateSpy));
        when(eventStore.findAllByAggregateIdOrderByTimestampAscStartFrom(aggregateId, 5))
            .thenReturn(List.of(event));
        when(handlerRegistry.getEventSourcingHandler(event.payloadClass()))
            .thenReturn(eventSourcingHandler);
        when(eventSourcingHandler.apply(event, initialAggregateSpy))
            .thenReturn(updatedAggregateSpy);

        // Act
        Aggregate result = 
            aggregateService.loadAggregateState(aggregateId, Collections.emptyList());

        // Assert
        assertEquals(updatedAggregateSpy, result);
        verify(eventStore).findAllByAggregateIdOrderByTimestampAscStartFrom(aggregateId, 5);
        verify(eventStore, never()).findAllByAggregateIdOrderByTimestampAsc(anyString());
    }

    @Test
    void loadPreviousAggregateState_WithoutSnapshotEnabled_ShouldLoadAllEvents() {
        // Arrange
        String aggregateId = "test-aggregate-id";
        TestPayload initialPayload = new TestPayload(aggregateId, "initial");
        TestPayload updatedPayload = new TestPayload(aggregateId, "updated");

        Aggregate initialAggregate = 
            new Aggregate(0L, initialPayload, aggregateId);
        Aggregate updatedAggregate = 
            new Aggregate(1L, updatedPayload, aggregateId);

        // Spy on the aggregates to mock snapshot behavior
        Aggregate initialAggregateSpy = Mockito.spy(initialAggregate);
        Aggregate updatedAggregateSpy = Mockito.spy(updatedAggregate);

        Mockito.when(initialAggregateSpy.isSnapshotEnabled()).thenReturn(false);
        Mockito.when(updatedAggregateSpy.isSnapshotEnabled()).thenReturn(false);

        Event event = new Event("event-id", new TestEvent("update"), Instant.now(), aggregateId);

        when(aggregateStore.findTopByAggregateIdOrderByVersionDesc(aggregateId))
            .thenReturn(Optional.of(initialAggregateSpy));
        when(eventStore.findAllByAggregateIdOrderByTimestampAsc(aggregateId))
            .thenReturn(List.of(event));
        when(handlerRegistry.getEventSourcingHandler(event.payloadClass()))
            .thenReturn(eventSourcingHandler);
        when(eventSourcingHandler.apply(event, initialAggregateSpy))
            .thenReturn(updatedAggregateSpy);

        // Act
        Aggregate result = 
            aggregateService.loadAggregateState(aggregateId, Collections.emptyList());

        // Assert
        assertEquals(updatedAggregateSpy, result);
        verify(eventStore).findAllByAggregateIdOrderByTimestampAsc(aggregateId);
        verify(eventStore, never()).findAllByAggregateIdOrderByTimestampAscStartFrom(anyString(), anyInt());
    }

    // Test payload classes
    @Getter
    private static class TestPayload {
        private final String id;
        private final String value;

        public TestPayload(String id, String value) {
            this.id = id;
            this.value = value;
        }

    }

    @Getter
    private static class TestEvent {
        private final String action;

        public TestEvent(String action) {
            this.action = action;
        }

    }
}
