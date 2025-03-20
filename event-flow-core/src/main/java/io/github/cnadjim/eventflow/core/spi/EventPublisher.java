package io.github.cnadjim.eventflow.core.spi;

import io.github.cnadjim.eventflow.core.domain.EventWrapper;

import java.util.List;

public interface EventPublisher {
    void publish(EventWrapper event);
    void publishAll(List<EventWrapper> events);
}
