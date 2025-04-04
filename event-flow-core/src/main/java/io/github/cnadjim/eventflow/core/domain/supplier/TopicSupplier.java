package io.github.cnadjim.eventflow.core.domain.supplier;

import io.github.cnadjim.eventflow.core.domain.Topic;

@FunctionalInterface
public interface TopicSupplier {

    Topic topic();

}
