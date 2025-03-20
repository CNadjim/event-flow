package io.github.cnadjim.eventflow.core.api;

import io.github.cnadjim.eventflow.core.domain.handler.HandlerInvoker;

public interface RegisterHandler {

    <HANDLER extends HandlerInvoker> void registerHandler(Class<?> messagePayloadClass, HANDLER handler);

    void scanInstance(Object instance);

    void scanPackage(String packageName);
}
