package io.github.cnadjim.eventflow.core.spi;

import io.github.cnadjim.eventflow.core.domain.Event;

import java.util.List;

public interface EventPublisher {
    void publish(Event event);
    void publishAll(List<Event> events);
}
