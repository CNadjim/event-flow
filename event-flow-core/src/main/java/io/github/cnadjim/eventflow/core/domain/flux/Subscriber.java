package io.github.cnadjim.eventflow.core.domain.flux;

public interface Subscriber<ITEM> {
    void onSubscribe(Subscription subscription);
    void onNext(ITEM item);
    void onError(Throwable throwable);
    void onComplete();
}
