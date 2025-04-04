package io.github.cnadjim.customer;

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

    @HandleEvent
    public void on(CustomerEvent.CustomerBirthdayUpdatedEvent customerBirthdayUpdatedEvent) {
        throw new RuntimeException("exception");
    }
}
