package io.github.cnadjim.eventflow.core.port;

import io.github.cnadjim.eventflow.core.domain.message.Event;

import java.util.Collection;

/**
 * Service Provider Interface for event storage.
 * The event store is responsible for persisting and retrieving events in an event-sourced system.
 * It provides methods to save events, delete events, and find events by aggregate ID.
 */
public interface EventStore {

    /**
     * Saves an event to the store.
     *
     * @param event The event to save
     */
    void save(Event event);

    /**
     * Deletes all events associated with a specific aggregate ID.
     *
     * @param aggregateId The ID of the aggregate whose events should be deleted
     */
    void deleteAllByAggregateId(String aggregateId);

    /**
     * Finds all events for a specific aggregate ID, ordered by timestamp in ascending order.
     *
     * @param aggregateId The ID of the aggregate whose events should be retrieved
     * @return An Iterable of events for the specified aggregate ID
     */
    Collection<Event> findAllByAggregateIdOrderByTimestampAsc(String aggregateId);

    /**
     * Finds all events for a specific aggregate ID, ordered by timestamp in ascending order,
     * starting from a specific position.
     *
     * @param aggregateId The ID of the aggregate whose events should be retrieved
     * @param startFrom The position to start retrieving events from
     * @return An Iterable of events for the specified aggregate ID, starting from the specified position
     */
    Collection<Event> findAllByAggregateIdOrderByTimestampAscStartFrom(String aggregateId, int startFrom);
}
