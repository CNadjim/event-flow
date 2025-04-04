package io.github.cnadjim.eventflow.core.domain.handler;

import io.github.cnadjim.eventflow.core.domain.Command;
import io.github.cnadjim.eventflow.core.domain.Event;
import io.github.cnadjim.eventflow.core.domain.exception.HandlerExecutionException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

public interface CommandHandler extends Handler {
    List<Event> handle(Command command) throws HandlerExecutionException;

    static CommandHandler create(Class<?> payloadClass, Object instance, Method method) {
        return new CommandHandler() {

            @Override
            public Class<?> payloadClass() {
                return payloadClass;
            }

            @Override
            public List<Event> handle(Command command) throws HandlerExecutionException {
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
                    throw new HandlerExecutionException(String.format("Command handler %s returned null or empty result", method.getName()));
                }

                return events;
            }
        };
    }
}
