package io.github.cnadjim.eventflow.core.domain;

import io.github.cnadjim.eventflow.core.domain.supplier.PayloadSupplier;

public interface PayloadWrapper extends PayloadSupplier {

    Object payload();
}
