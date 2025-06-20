package io.github.cnadjim.eventflow.core.domain.flux;

import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;


public interface MessageSubscriber extends Subscriber<Message> {

    Topic topic();

    String handlerName();

    default void onError(Throwable throwable) {

    }

    default void onComplete() {

    }
}
