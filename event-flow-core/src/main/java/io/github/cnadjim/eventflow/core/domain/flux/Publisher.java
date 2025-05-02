package io.github.cnadjim.eventflow.core.domain.flux;

public interface Publisher<SUBSCRIBER extends Subscriber<?>> {

    void subscribe(SUBSCRIBER subscriber);
}
