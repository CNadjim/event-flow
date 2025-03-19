package io.github.cnadjim.eventflow.core.stub;

import io.github.cnadjim.eventflow.annotation.Stub;
import io.github.cnadjim.eventflow.core.domain.supplier.MessageTypeSupplier;
import org.apache.commons.lang3.StringUtils;
import io.github.cnadjim.eventflow.core.spi.TopicRegistry;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import static java.util.Objects.isNull;

@Stub
public class InMemoryTopicRegistry implements TopicRegistry {
    private final Set<String> commandTopics = new ConcurrentSkipListSet<>();
    private final Set<String> eventTopics = new ConcurrentSkipListSet<>();
    private final Set<String> queryTopics = new ConcurrentSkipListSet<>();

    @Override
    public void addTopic(MessageTypeSupplier.MessageType messageType, String topic) {
        if (isNull(messageType) || StringUtils.isBlank(topic)) {
            return;
        }

        switch (messageType) {
            case COMMAND -> commandTopics.add(topic);
            case EVENT -> eventTopics.add(topic);
            case QUERY -> queryTopics.add(topic);
        }
    }

    @Override
    public Set<String> getTopics(MessageTypeSupplier.MessageType messageType) {
        if (isNull(messageType)) {
            return Collections.emptySet();
        }

        return switch (messageType) {
            case COMMAND -> commandTopics;
            case EVENT -> eventTopics;
            case QUERY -> queryTopics;
        };
    }
}
