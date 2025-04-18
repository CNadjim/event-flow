package io.github.cnadjim.eventflow.core.api;

import io.github.cnadjim.eventflow.annotation.UseCase;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;

import java.util.Collection;

/**
 * {@code FindTopic} defines the use case for retrieving all available topics.
 */
@UseCase
public interface FindTopic {

    /**
     * Retrieves all available topics.
     *
     * @return A collection of {@link Topic} objects.
     */
    Collection<Topic> findAll();
}
