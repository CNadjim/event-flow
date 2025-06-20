package io.github.cnadjim.eventflow.core.domain.handler;

import io.github.cnadjim.eventflow.core.domain.aggregate.Aggregate;
import io.github.cnadjim.eventflow.core.domain.exception.HandlerExecutionException;
import io.github.cnadjim.eventflow.core.domain.message.Command;
import io.github.cnadjim.eventflow.core.domain.message.Event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public record DefaultCommandHandler(Class<?> payloadClass, Object instance, Method method) implements CommandHandler {

    @Override
    public List<Event> handle(Command command, Aggregate aggregate) throws HandlerExecutionException {
        final Object result = invoke(aggregate.payload(), method, command.payload());

        if (isNull(result)) {
            throw new HandlerExecutionException(String.format("Command handler %s returned null result", method.getName()));
        }

        final List<Event> events = new ArrayList<>();

        if (result instanceof Collection<?> results) {
            for (Object eventPayload : results) {
                final Event event = new Event(eventPayload);
                events.add(event);
            }
        } else {
            final Event event = new Event(result);
            events.add(event);
        }

        return events;
    }
}
