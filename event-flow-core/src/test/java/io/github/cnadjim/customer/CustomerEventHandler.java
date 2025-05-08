package io.github.cnadjim.customer;

import io.github.cnadjim.eventflow.annotation.EventHandler;

public class CustomerEventHandler {

    private final Object injectDependency;

    public CustomerEventHandler(Object injectDependency) {
        this.injectDependency = injectDependency;
    }

    @EventHandler
    public void on(CustomerEvent.CustomerCreatedEvent customerCreatedEvent) {
        String string = injectDependency.toString();
    }

    @EventHandler
    public void on(CustomerEvent.CustomerNameUpdatedEvent customerNameUpdatedEvent) {
    }

    @EventHandler
    public void on(CustomerEvent.CustomerBirthdayUpdatedEvent customerBirthdayUpdatedEvent) {
        throw new RuntimeException("exception");
    }
}
