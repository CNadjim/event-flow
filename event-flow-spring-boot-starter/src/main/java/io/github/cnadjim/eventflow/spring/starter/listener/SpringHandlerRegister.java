package io.github.cnadjim.eventflow.spring.starter.listener;

import io.github.cnadjim.eventflow.core.domain.handler.Handler;
import io.github.cnadjim.eventflow.core.service.HandlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class SpringHandlerRegister implements BeanPostProcessor {
    private final HandlerService handlerService;

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        final String mainApplicationPackageName = event.getSpringApplication().getMainApplicationClass().getPackageName();
        final Collection<Handler> handlers = handlerService.scan(mainApplicationPackageName);
        handlers.forEach(handlerService::register);
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull final Object bean, @NonNull final String beanName) {
        final Collection<Handler> handlers = handlerService.scan(bean);
        handlers.forEach(handlerService::register);
        return bean;
    }
}
