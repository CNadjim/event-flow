package io.github.cnadjim.eventflow.spring.starter.listener;

import io.github.cnadjim.eventflow.core.api.RegisterHandler;
import io.github.cnadjim.eventflow.annotation.HandleEvent;
import io.github.cnadjim.eventflow.annotation.HandleQuery;
import io.github.cnadjim.eventflow.core.domain.handler.EventHandler;
import io.github.cnadjim.eventflow.core.domain.handler.QueryHandler;
import io.github.cnadjim.eventflow.core.spi.EventSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
public class ApplicationReadyListener implements BeanPostProcessor {

    private final RegisterHandler registerHandler;
    private final EventSubscriber eventSubscriber;

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        final String mainApplicationPackageName = event.getSpringApplication().getMainApplicationClass().getPackageName();
        registerHandler.scanPackage(mainApplicationPackageName);
        eventSubscriber.start();
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull final Object bean, @NonNull final String beanName) {
        searchHandlers(bean);
        return bean;
    }

    private <A extends Annotation> List<Method> findMethodsWithAnnotation(Class<?> c, Class<A> annotation) {
        final List<Method> methods = new ArrayList<>();
        for (final Method method : c.getDeclaredMethods()) {
            final A result = AnnotationUtils.findAnnotation(method, annotation);
            if (nonNull(result)) {
                methods.add(method);
            }
        }
        return methods;
    }

    private void searchHandlers(Object bean) {
        findMethodsWithAnnotation(bean.getClass(), HandleQuery.class).forEach(method -> addQueryHandler(bean, method));
        findMethodsWithAnnotation(bean.getClass(), HandleEvent.class).forEach(method -> addEventHandler(bean, method));
    }

    private void addEventHandler(Object bean, Method method) {
        if (method.getParameterCount() >= 1) {
            Class<?> eventType = method.getParameters()[0].getType();
            EventHandler eventHandler = EventHandler.create(bean, method);
            registerHandler.registerEventHandler(eventType, eventHandler);
        }
    }

    private void addQueryHandler(Object bean, Method method) {
        if (method.getParameterCount() >= 1) {
            Class<?> queryType = method.getParameters()[0].getType();
            QueryHandler queryHandler = QueryHandler.create(bean, method);
            registerHandler.registerQueryHandler(queryType, queryHandler);
        }
    }

}
