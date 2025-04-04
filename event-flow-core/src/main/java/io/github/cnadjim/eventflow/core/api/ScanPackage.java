package io.github.cnadjim.eventflow.core.api;

import io.github.cnadjim.eventflow.annotation.UseCase;
import io.github.cnadjim.eventflow.core.domain.handler.Handler;

import java.util.Collection;

@UseCase
public interface ScanPackage {

    Collection<Handler> scan(String packageName);
}
