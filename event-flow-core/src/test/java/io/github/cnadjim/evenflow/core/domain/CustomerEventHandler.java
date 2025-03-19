package io.github.cnadjim.evenflow.core.domain;

import io.github.cnadjim.eventflow.annotation.HandleEvent;

public class CustomerEventHandler {

    private final Object injectDependency;

    public CustomerEventHandler(Object injectDependency) {
        this.injectDependency = injectDependency;
    }

    @HandleEvent
    public void on(CustomerEvent.CustomerCreatedEvent customerCreatedEvent) {
        String string = injectDependency.toString();
    }

    @HandleEvent
    public void on(CustomerEvent.CustomerNameUpdatedEvent customerNameUpdatedEvent) {
    }
}
