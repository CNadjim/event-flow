package io.github.cnadjim.eventflow.core.api;

import io.github.cnadjim.eventflow.annotation.UseCase;
import io.github.cnadjim.eventflow.core.domain.handler.Handler;

import java.util.Collection;
import java.util.Collections;

@UseCase
public interface ScanObject {

    Collection<Handler> scan(Object instance);

    default Collection<Handler> scan(Object instance, Class<? extends Handler> handlerClass) {
        return scan(instance, Collections.singleton(handlerClass));
    }

    default Collection<Handler> scan(Object instance, Collection<Class<? extends Handler>> handlerClasses) {
        return scan(instance)
                .stream()
                .filter(predicate -> handlerClasses.contains(predicate.getClass()))
                .toList();
    }
}
