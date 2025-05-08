package io.github.cnadjim.eventflow.core.domain.handler;

import io.github.cnadjim.eventflow.core.domain.exception.HandlerExecutionException;
import io.github.cnadjim.eventflow.core.domain.message.Event;

public interface EventHandler extends Handler {

    void onEvent(Event event) throws HandlerExecutionException;
}
