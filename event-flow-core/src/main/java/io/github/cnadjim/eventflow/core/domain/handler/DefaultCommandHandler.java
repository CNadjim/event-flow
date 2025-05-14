package io.github.cnadjim.eventflow.core.domain.handler;

import io.github.cnadjim.eventflow.core.domain.aggregate.Aggregate;
import io.github.cnadjim.eventflow.core.domain.exception.HandlerExecutionException;
import io.github.cnadjim.eventflow.core.domain.message.Command;
import io.github.cnadjim.eventflow.core.domain.message.Event;

import java.lang.reflect.Method;
import java.util.Collection;

import static java.util.Objects.nonNull;

public record DefaultCommandHandler(Class<?> payloadClass, Object instance, Method method) implements CommandHandler {

    @Override
    public Event handle(Command command, Aggregate aggregate) throws HandlerExecutionException {
        final Object result = invoke(aggregate.payload(), method, command.payload());
        if (result instanceof Collection<?>) {
            throw new HandlerExecutionException(String.format("Command handler %s returned collection result", method.getName()));
        } else if (nonNull(result)) {
            return new Event(result);
        } else {
            throw new HandlerExecutionException(String.format("Command handler %s returned null result", method.getName()));
        }
    }
}
