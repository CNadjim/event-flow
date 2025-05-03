package io.github.cnadjim.eventflow.core.domain.log;

import static org.slf4j.LoggerFactory.getLogger;

public interface Logger {

    default org.slf4j.Logger logger() {
        return getLogger(this.getClass());
    }
}
