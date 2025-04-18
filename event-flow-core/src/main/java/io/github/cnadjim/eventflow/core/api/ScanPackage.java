package io.github.cnadjim.eventflow.core.api;

import io.github.cnadjim.eventflow.annotation.UseCase;
import io.github.cnadjim.eventflow.core.domain.handler.Handler;

import java.util.Collection;

/**
 * {@code ScanPackage} defines the use case for scanning a package for handlers.
 * <p>
 * It allows the system to automatically discover and register handlers within a given package.
 */
@UseCase
public interface ScanPackage {

    /**
     * Scans a package for handlers.
     *
     * @param packageName The name of the package to scan.
     * @return A collection of {@link Handler} objects found in the package.
     */
    Collection<Handler> scan(String packageName);
}
