package io.github.cnadjim.eventflow.core.spi;

public interface EventSubscriber {
    void subscribe(String topic);
    void start();
    void stop();
}
