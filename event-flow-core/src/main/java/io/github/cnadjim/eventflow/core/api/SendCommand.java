package io.github.cnadjim.eventflow.core.api;

import java.util.concurrent.CompletableFuture;

public interface SendCommand {
    CompletableFuture<String> sendCommand(Object command);
}
