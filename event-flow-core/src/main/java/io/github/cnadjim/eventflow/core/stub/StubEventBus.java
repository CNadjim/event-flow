package io.github.cnadjim.eventflow.core.stub;

import io.github.cnadjim.eventflow.core.api.SendEvent;
import io.github.cnadjim.eventflow.annotation.Stub;
import io.github.cnadjim.eventflow.core.domain.EventWrapper;
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
    public void publish(EventWrapper event) {
        if (subscribedTopics.contains(event.topic())) {
            sendEvent.send(event);
        }
    }

    @Override
    public void publishAll(List<EventWrapper> events) {
        for (EventWrapper event : events) {
            publish(event);
        }
    }
}
