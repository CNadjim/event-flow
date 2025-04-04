package io.github.cnadjim.eventflow.spring.starter.pagination;

import io.github.cnadjim.eventflow.core.domain.Page;

public interface PageAdaptor {

    static <T> Page<T> ofSpringPage(org.springframework.data.domain.Page<T> springPage) {
        return new Page<>(
                springPage.getContent(),
                springPage.getTotalElements(),
                springPage.getTotalPages(),
                springPage.getNumber(),
                springPage.getSize()
        );
    }
}
