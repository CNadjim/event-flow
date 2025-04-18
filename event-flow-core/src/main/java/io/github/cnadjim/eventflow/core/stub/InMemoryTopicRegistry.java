package io.github.cnadjim.eventflow.core.stub;

import io.github.cnadjim.eventflow.annotation.Stub;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;
import io.github.cnadjim.eventflow.core.spi.TopicRegistry;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

@Stub
public class InMemoryTopicRegistry implements TopicRegistry {

    final CopyOnWriteArraySet<Topic> topics = new CopyOnWriteArraySet<>();

    @Override
    public void register(Topic topic) {
        topics.add(topic);
    }

    @Override
    public Collection<Topic> findAll() {
        return topics.stream().toList();
    }
}
