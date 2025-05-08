package io.github.cnadjim.eventflow.spring.starter.adaptor;

import io.github.cnadjim.eventflow.core.domain.pagination.Page;

import java.util.function.Function;

public interface SpringPageAdaptor {

    <SPRING_PAGE_TYPE, EVENT_FLOW_PAGE_TYPE> Page<EVENT_FLOW_PAGE_TYPE> fromSpring(org.springframework.data.domain.Page<SPRING_PAGE_TYPE> springPage, Function<SPRING_PAGE_TYPE, EVENT_FLOW_PAGE_TYPE> mapping);

    default <EVENT_FLOW_PAGE_TYPE> Page<EVENT_FLOW_PAGE_TYPE> fromSpring(org.springframework.data.domain.Page<EVENT_FLOW_PAGE_TYPE> springPage) {
        return fromSpring(springPage, (element -> element));
    }
}
