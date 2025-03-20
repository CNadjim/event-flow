package io.github.cnadjim.eventflow.core.service;

import io.github.cnadjim.eventflow.annotation.*;
import io.github.cnadjim.eventflow.core.api.RegisterHandler;
import io.github.cnadjim.eventflow.core.domain.exception.EventFlowIllegalArgumentException;
import io.github.cnadjim.eventflow.core.domain.exception.ScanPackageExecutionException;
import io.github.cnadjim.eventflow.core.domain.handler.*;
import io.github.cnadjim.eventflow.core.spi.EventSubscriber;
import io.github.cnadjim.eventflow.core.spi.HandlerRegistry;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Optional;

@DomainService
public class HandlerService implements RegisterHandler {
    private final EventSubscriber eventSubscriber;
    private final HandlerRegistry handlerRegistry;

    public HandlerService(EventSubscriber eventSubscriber,
                           HandlerRegistry handlerRegistry) {
        this.eventSubscriber = eventSubscriber;
        this.handlerRegistry = handlerRegistry;
    }

    @Override
    public <HANDLER extends HandlerInvoker> void registerHandler(Class<?> messagePayloadClass, HANDLER handler) {
        switch (handler){
            case EventHandler eventHandler -> registerEventHandler(messagePayloadClass, eventHandler);
            case QueryHandler queryHandler -> registerQueryHandler(messagePayloadClass, queryHandler);
            case CommandHandler commandHandler -> registerCommandHandler(messagePayloadClass, commandHandler);
            case EventSourcingHandler eventSourcingHandler -> registerEventSourcingHandler(messagePayloadClass, eventSourcingHandler);
            default -> throw new EventFlowIllegalArgumentException("Unexpected value: " + handler);
        }
    }

    private void registerCommandHandler(Class<?> messagePayloadClass, CommandHandler commandHandler) {
        handlerRegistry.registerHandler(messagePayloadClass, commandHandler);
    }

    private void registerEventHandler(Class<?> messagePayloadClass, EventHandler eventHandler) {
        eventSubscriber.subscribe(messagePayloadClass.getSimpleName());
        handlerRegistry.registerHandler(messagePayloadClass, eventHandler);
    }

    private void registerQueryHandler(Class<?> messagePayloadClass, QueryHandler queryHandler) {
        handlerRegistry.registerHandler(messagePayloadClass, queryHandler);
    }

    private void registerEventSourcingHandler(Class<?> messagePayloadClass, EventSourcingHandler eventSourcingHandler) {
        eventSubscriber.subscribe(messagePayloadClass.getSimpleName());
        handlerRegistry.registerHandler(messagePayloadClass, eventSourcingHandler);
    }

    @Override
    public void scanInstance(Object instance) {
        if (instance == null) {
            return;
        }

        final Class<?> clazz = instance.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(HandleEvent.class)) {
                Class<?> eventType = method.getParameterTypes()[0];
                EventHandler eventHandler = EventHandler.create(instance, method);
                registerEventHandler(eventType, eventHandler);
            } else if (method.isAnnotationPresent(HandleQuery.class)) {
                Class<?> eventType = method.getParameterTypes()[0];
                QueryHandler queryHandler = QueryHandler.create(instance, method);
                registerQueryHandler(eventType, queryHandler);
            } else if (method.isAnnotationPresent(HandleCommand.class)) {
                Class<?> eventType = method.getParameterTypes()[0];
                CommandHandler commandHandler = CommandHandler.create(instance, method);
                registerCommandHandler(eventType, commandHandler);
            } else if(method.isAnnotationPresent(ApplyEvent.class)) {
                Class<?> eventType = method.getParameterTypes()[0];
                EventSourcingHandler eventSourcingHandler = EventSourcingHandler.create(instance, method);
                registerEventSourcingHandler(eventType, eventSourcingHandler);
            }
        }
    }

    @Override
    public void scanPackage(String packageName) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');

            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String decodedPath = URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8);
                File file = new File(decodedPath);
                if (file.isDirectory()) {
                    processDirectory(packageName, file);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Package scan failed", e);
        }
    }

    private void processDirectory(String pkg, File directory) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                processDirectory(pkg + "." + file.getName(), file);
            } else if (file.getName().endsWith(".class")) {
                processClassFile(pkg, file);
            }
        }
    }

    private void processClassFile(String pkg, File file) {
        final String className = pkg + '.' + file.getName().replace(".class", "");
        try {
            Class<?> clazz = Class.forName(className);
            registerCommandHandlers(clazz);
            registerEventSourcingHandlers(clazz);
            registerEventHandlers(clazz);
            registerQueryHandlers(clazz);
        } catch (Exception exception) {
            throw new ScanPackageExecutionException(exception);
        }
    }

    private void registerQueryHandlers(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(HandleQuery.class)) {
                Class<?> queryType = method.getParameterTypes()[0];
                instance(clazz).ifPresent(instance -> {
                    QueryHandler queryHandler = QueryHandler.create(instance, method);
                    registerQueryHandler(queryType, queryHandler);
                });
            }
        }
    }

    private void registerCommandHandlers(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(HandleCommand.class)) {
                Class<?> commandType = method.getParameterTypes()[0];
                instance(clazz).ifPresent(instance -> {
                    CommandHandler commandHandler = CommandHandler.create(instance, method);
                    registerCommandHandler(commandType, commandHandler);
                });
            }
        }
    }

    private void registerEventSourcingHandlers(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ApplyEvent.class)) {
                Class<?> eventType = method.getParameterTypes()[0];
                instance(clazz).ifPresent(instance -> {
                    EventSourcingHandler eventSourcingHandler = EventSourcingHandler.create(instance, method);
                    registerEventSourcingHandler(eventType, eventSourcingHandler);
                });
            }
        }
    }

    private void registerEventHandlers(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(HandleEvent.class)) {
                Class<?> eventType = method.getParameterTypes()[0];
                instance(clazz).ifPresent(instance -> {
                    EventHandler eventHandler = EventHandler.create(instance, method);
                    registerEventHandler(eventType, eventHandler);
                });
            }
        }
    }

    public static Optional<Object> instance(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return Optional.of(constructor.newInstance());
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException ignored) {
            return Optional.empty();
        }
    }
}
