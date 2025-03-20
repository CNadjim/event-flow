package io.github.cnadjim.eventflow.core.api;

import io.github.cnadjim.eventflow.core.domain.EventWrapper;

public interface SendEvent {
    void send(EventWrapper event);
}
