package io.github.cnadjim.eventflow.core.stub;

import io.github.cnadjim.eventflow.core.api.SendEvent;
import io.github.cnadjim.eventflow.core.ddd.Stub;
import io.github.cnadjim.eventflow.core.domain.Event;
import io.github.cnadjim.eventflow.core.spi.EventPublisher;
import io.github.cnadjim.eventflow.core.spi.EventSubscriber;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Stub
public class StubEventBus implements EventPublisher, EventSubscriber {

    private final SendEvent sendEvent;
    private final Set<String> subscribedTopics = new ConcurrentSkipListSet<>();

    public StubEventBus(final SendEvent sendEvent) {
        this.sendEvent = sendEvent;
    }

    @Override
    public void subscribe(String topic) {
        this.subscribedTopics.add(topic);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void publish(Event event) {
        if (subscribedTopics.contains(event.topic())) {
            sendEvent.send(event);
        }
    }

    @Override
    public void publishAll(List<Event> events) {
        for (Event event : events) {
            publish(event);
        }
    }
}
