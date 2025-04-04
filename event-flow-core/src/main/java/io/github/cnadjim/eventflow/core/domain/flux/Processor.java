package io.github.cnadjim.eventflow.core.domain.flux;

public interface Processor<SUBSCRIBER_ITEM, PUBLISHER_ITEM> extends Subscriber<SUBSCRIBER_ITEM>, Publisher<PUBLISHER_ITEM> {
}
