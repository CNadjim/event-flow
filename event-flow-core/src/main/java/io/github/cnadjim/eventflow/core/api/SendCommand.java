package io.github.cnadjim.eventflow.core.api;

import io.github.cnadjim.eventflow.annotation.UseCase;

import java.util.concurrent.CompletableFuture;

@UseCase
public interface SendCommand {

    CompletableFuture<String> send(Object command);
}
