package io.github.cnadjim.eventflow.core.service;

import io.github.cnadjim.eventflow.core.api.RegisterHandler;
import io.github.cnadjim.eventflow.core.ddd.DomainService;
import io.github.cnadjim.eventflow.core.domain.annotation.ApplyEvent;
import io.github.cnadjim.eventflow.core.domain.annotation.HandleCommand;
import io.github.cnadjim.eventflow.core.domain.annotation.HandleEvent;
import io.github.cnadjim.eventflow.core.domain.annotation.HandleQuery;
import io.github.cnadjim.eventflow.core.domain.exception.ScanPackageExecutionException;
import io.github.cnadjim.eventflow.core.domain.handler.*;
import io.github.cnadjim.eventflow.core.domain.supplier.MessageTypeSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.TopicSupplier;
import io.github.cnadjim.eventflow.core.spi.EventSubscriber;
import io.github.cnadjim.eventflow.core.spi.HandlerRegistry;
import io.github.cnadjim.eventflow.core.spi.TopicRegistry;

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
    private final TopicRegistry topicRegistry;
    private final HandlerRegistry handlerRegistry;
    private final EventSubscriber eventSubscriber;

    public HandlerService(TopicRegistry topicRegistry, HandlerRegistry handlerRegistry, EventSubscriber eventSubscriber) {
        this.topicRegistry = topicRegistry;
        this.eventSubscriber = eventSubscriber;
        this.handlerRegistry = handlerRegistry;
    }

    @Override
    public void registerCommandHandler(Class<?> messagePayloadClass, CommandHandler commandHandler) {
        final String commandTopic = TopicSupplier.findTopic(messagePayloadClass).orElseGet(messagePayloadClass::getName);
        topicRegistry.addTopic(MessageTypeSupplier.MessageType.COMMAND, commandTopic);
        handlerRegistry.registerHandler(messagePayloadClass, commandHandler);
    }

    @Override
    public void registerEventHandler(Class<?> messagePayloadClass, EventHandler eventHandler) {
        final String eventTopic = TopicSupplier.findTopic(messagePayloadClass).orElseGet(messagePayloadClass::getName);
        topicRegistry.addTopic(MessageTypeSupplier.MessageType.EVENT, eventTopic);
        eventSubscriber.subscribe(eventTopic);
        handlerRegistry.registerHandler(messagePayloadClass, eventHandler);
    }

    @Override
    public void registerQueryHandler(Class<?> messagePayloadClass, QueryHandler queryHandler) {
        handlerRegistry.registerHandler(messagePayloadClass, queryHandler);
    }

    @Override
    public void registerEventSourcingHandler(Class<?> messagePayloadClass, EventSourcingHandler eventSourcingHandler) {
        handlerRegistry.registerHandler(messagePayloadClass, eventSourcingHandler);
    }

    @Override
    public void registerEventHandler(Object eventHandlerInstance) {
        final Class<?> clazz = eventHandlerInstance.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(HandleEvent.class)) {
                Class<?> eventType = method.getParameterTypes()[0];
                EventHandler eventHandler = EventHandler.create(eventHandlerInstance, method);
                registerEventHandler(eventType, eventHandler);
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
