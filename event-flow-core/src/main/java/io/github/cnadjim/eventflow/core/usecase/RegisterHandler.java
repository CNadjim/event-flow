package io.github.cnadjim.eventflow.core.usecase;

import io.github.cnadjim.eventflow.annotation.UseCase;
import io.github.cnadjim.eventflow.core.domain.handler.Handler;

/**
 * {@code RegisterHandler} defines the use case for registering a handler.
 * <p>
 * It allows for registering different types of handlers within the system.
 */
@UseCase
public interface RegisterHandler {

    /**
     * Registers a handler.
     *
     * @param handler The {@link Handler} to register.
     * @param <HANDLER> The type of the handler.
     */
    <HANDLER extends Handler> void register(HANDLER handler);
}
