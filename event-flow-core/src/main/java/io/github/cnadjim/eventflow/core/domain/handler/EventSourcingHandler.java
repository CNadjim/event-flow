package io.github.cnadjim.eventflow.core.domain.handler;

import io.github.cnadjim.eventflow.core.domain.aggregate.Aggregate;
import io.github.cnadjim.eventflow.core.domain.exception.HandlerExecutionException;
import io.github.cnadjim.eventflow.core.domain.message.Event;

public interface EventSourcingHandler extends Handler {

    Aggregate apply(Event event, Aggregate aggregate) throws HandlerExecutionException;
}
