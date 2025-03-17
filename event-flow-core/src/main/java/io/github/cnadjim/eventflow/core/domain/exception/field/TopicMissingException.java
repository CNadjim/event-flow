package io.github.cnadjim.eventflow.core.domain.exception.field;

import static io.github.cnadjim.eventflow.core.domain.supplier.TopicSupplier.TOPIC_FIELD;

public class TopicMissingException extends MissingFieldException {

    public TopicMissingException() {
        super(TOPIC_FIELD);
    }
}
