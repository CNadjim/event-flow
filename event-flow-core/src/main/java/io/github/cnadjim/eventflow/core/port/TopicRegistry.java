package io.github.cnadjim.eventflow.core.port;

import io.github.cnadjim.eventflow.core.domain.topic.Topic;

import java.util.Collection;

/**
 * Service Provider Interface for topic registry implementations.
 * The topic registry is responsible for storing and retrieving topics used in the event flow system.
 * Topics are used to categorize and route messages to the appropriate handlers.
 */
public interface TopicRegistry {

    /**
     * Registers a topic in the registry.
     *
     * @param topic The topic to register
     */
    void register(Topic topic);

    /**
     * Retrieves all registered topics.
     *
     * @return A collection of all registered topics
     */
    Collection<Topic> findAll();
}
