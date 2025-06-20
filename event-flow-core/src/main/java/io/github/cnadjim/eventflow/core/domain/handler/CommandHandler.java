package io.github.cnadjim.eventflow.core.domain.handler;

import io.github.cnadjim.eventflow.core.domain.aggregate.Aggregate;
import io.github.cnadjim.eventflow.core.domain.exception.HandlerExecutionException;
import io.github.cnadjim.eventflow.core.domain.message.Command;
import io.github.cnadjim.eventflow.core.domain.message.Event;

import java.util.List;

public interface CommandHandler extends Handler {

    List<Event> handle(Command command, Aggregate aggregate) throws HandlerExecutionException;
}
