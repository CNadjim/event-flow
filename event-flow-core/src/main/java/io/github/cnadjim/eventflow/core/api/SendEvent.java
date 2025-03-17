package io.github.cnadjim.eventflow.core.api;

import io.github.cnadjim.eventflow.core.domain.Event;

public interface SendEvent {
    void send(Event event);
}
