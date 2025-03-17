package io.github.cnadjim.eventflow.core.domain.handler;

import io.github.cnadjim.eventflow.core.domain.Command;
import io.github.cnadjim.eventflow.core.domain.Event;
import io.github.cnadjim.eventflow.core.domain.exception.handler.CommandHandlerExecutionException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static io.github.cnadjim.eventflow.core.domain.handler.HandlerInvoker.invoke;
import static java.util.Objects.nonNull;

@FunctionalInterface
public interface CommandHandler extends HandlerInvoker {
    List<Event> handle(Command command) throws CommandHandlerExecutionException;

    static CommandHandler create(Object instance, Method method) {
        return (command) -> {

            final Object payload = command.payload();
            final Object result = invoke(instance, method, payload);
            final List<Event> events = new ArrayList<>();

            if (result instanceof List<?> results) {
                for (Object event : results) {
                    events.add(Event.create(event));
                }
            } else if (nonNull(result)) {
                events.add(Event.create(result));
            } else {
                throw new CommandHandlerExecutionException(String.format("Command handler %s returned null or empty result", method.getName()));
            }

            return events;
        };
    }
}
