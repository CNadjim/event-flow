package io.github.cnadjim.eventflow.core.domain.flux;

public interface Publisher<T> {
    void subscribe(Subscriber<? super T> subscriber);
}
