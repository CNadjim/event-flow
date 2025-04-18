package io.github.cnadjim.eventflow.core.domain.supplier;


import io.github.cnadjim.eventflow.core.domain.topic.Topic;

/**
 * Functional interface for supplying topics.
 * This interface provides access to a topic, which is used for message routing and classification.
 */
@FunctionalInterface
public interface TopicSupplier {

    /**
     * Returns the topic.
     *
     * @return The topic
     */
    Topic topic();

}
