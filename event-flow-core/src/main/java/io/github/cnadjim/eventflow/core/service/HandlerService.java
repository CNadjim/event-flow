package io.github.cnadjim.eventflow.core.service;

import io.github.cnadjim.eventflow.annotation.*;
import io.github.cnadjim.eventflow.core.api.RegisterHandler;
import io.github.cnadjim.eventflow.core.api.ScanObject;
import io.github.cnadjim.eventflow.core.api.ScanPackage;
import io.github.cnadjim.eventflow.core.domain.handler.*;
import io.github.cnadjim.eventflow.core.service.dispatcher.CommandDispatcher;
import io.github.cnadjim.eventflow.core.service.dispatcher.EventDispatcher;
import io.github.cnadjim.eventflow.core.service.dispatcher.QueryDispatcher;
import io.github.cnadjim.eventflow.core.spi.HandlerRegistry;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@DomainService
public class HandlerService implements RegisterHandler, ScanPackage, ScanObject {
    private final HandlerRegistry handlerRegistry;

    private final EventDispatcher eventDispatcher;
    private final QueryDispatcher queryDispatcher;
    private final CommandDispatcher commandDispatcher;

    public HandlerService(HandlerRegistry handlerRegistry,
                          EventDispatcher eventDispatcher,
                          QueryDispatcher queryDispatcher,
                          CommandDispatcher commandDispatcher) {
        this.handlerRegistry = handlerRegistry;
        this.eventDispatcher = eventDispatcher;
        this.queryDispatcher = queryDispatcher;
        this.commandDispatcher = commandDispatcher;
    }

    @Override
    public <HANDLER extends Handler> void register(HANDLER handler) {

        if (isNull(handler)) throw new IllegalArgumentException("handler cannot be null");

        final Class<?> payloadClass = handler.payloadClass();

        if (isNull(payloadClass)) throw new IllegalArgumentException("payloadClass cannot be null");

        switch (handler) {
            case EventHandler eventHandler -> {
                handlerRegistry.registerHandler(eventHandler);
                eventDispatcher.subscribe(payloadClass);
            }
            case QueryHandler queryHandler -> {
                handlerRegistry.registerHandler(queryHandler);
                queryDispatcher.subscribe(payloadClass);
            }
            case CommandHandler commandHandler -> {
                handlerRegistry.registerHandler(commandHandler);
                commandDispatcher.subscribe(payloadClass);
            }
            case EventSourcingHandler eventSourcingHandler -> {
                handlerRegistry.registerHandler(eventSourcingHandler);
            }
            default -> throw new IllegalArgumentException("Unexpected value: " + handler);
        }
    }

    @Override
    public Collection<Handler> scan(Object instance) {
        final Collection<Handler> handlers = new ArrayList<>();

        if (isNull(instance)) {
            return handlers;
        }

        final Class<?> clazz = instance.getClass();

        for (Method method : clazz.getDeclaredMethods()) {
            final Class<?>[] methodParameterTypes = method.getParameterTypes();

            if (methodParameterTypes.length > 0) {
                final Class<?> messagePayloadClass = method.getParameterTypes()[0];

                if (method.isAnnotationPresent(HandleEvent.class)) {
                    final EventHandler eventHandler = EventHandler.create(messagePayloadClass, instance, method);
                    handlers.add(eventHandler);
                } else if (method.isAnnotationPresent(HandleQuery.class)) {
                    final QueryHandler queryHandler = QueryHandler.create(messagePayloadClass, instance, method);
                    handlers.add(queryHandler);
                } else if (method.isAnnotationPresent(HandleCommand.class)) {
                    final CommandHandler commandHandler = CommandHandler.create(messagePayloadClass, instance, method);
                    handlers.add(commandHandler);
                } else if (method.isAnnotationPresent(ApplyEvent.class)) {
                    final EventSourcingHandler eventSourcingHandler = EventSourcingHandler.create(messagePayloadClass, instance, method);
                    handlers.add(eventSourcingHandler);
                }
            }
        }

        return handlers;
    }


    @Override
    public Collection<Handler> scan(String packageName) {
        final List<Handler> handlers = new ArrayList<>();

        try {
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            final String path = packageName.replace('.', '/');
            final Enumeration<URL> resources = classLoader.getResources(path);

            final List<File> dirs = new ArrayList<>();

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }

            for (File directory : dirs) {
                handlers.addAll(findHandlers(directory, packageName));
            }

        } catch (IOException ignored) {
        }

        return handlers;
    }

    private static Optional<Object> tryCreateInstance(Class<?> clazz) {
        try {
            final Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return Optional.of(constructor.newInstance());
        } catch (Exception exception) {
            return Optional.empty();
        }
    }


    private List<Handler> findHandlers(File directory, String packageName) {
        List<Handler> handlers = new ArrayList<>();

        if (!directory.exists()) {
            return handlers;
        }

        final File[] files = directory.listFiles();

        if (nonNull(files)) {
            for (File file : files) {
                if (file.isDirectory()) {
                    handlers.addAll(findHandlers(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    final String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    try {
                        final Class<?> clazz = Class.forName(className);
                        if (isNotInterfaceOrAbstract(clazz)) {
                            tryCreateInstance(clazz).ifPresent(instance -> handlers.addAll(scan(instance)));
                        }
                    } catch (ClassNotFoundException ignored) {
                    }
                }
            }
        }

        return handlers;
    }

    private boolean isNotInterfaceOrAbstract(Class<?> clazz) {
        return !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers());
    }
}
