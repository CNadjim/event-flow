package io.github.cnadjim.eventflow.spring.starter.pagination;

import io.github.cnadjim.eventflow.core.domain.ResponseType;
import org.springframework.data.domain.Page;

public interface SpringResponseType {

    static <R> ResponseType<Page<R>> pageOf(Class<R> elementType) {
        return new SpringPageResponseType<>(elementType);
    }
}
