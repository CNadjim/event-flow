package io.github.cnadjim.eventflow.core.api;

import io.github.cnadjim.eventflow.annotation.UseCase;
import io.github.cnadjim.eventflow.core.domain.handler.Handler;

@UseCase
public interface RegisterHandler {

    <HANDLER extends Handler> void register(HANDLER handler);
}
