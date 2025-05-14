package io.github.cnadjim.eventflow.spring.starter.adaptor;

import io.github.cnadjim.eventflow.core.domain.pagination.DefaultPageRequest;
import io.github.cnadjim.eventflow.core.domain.pagination.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageRequestAdaptor implements SpringAdaptor<Pageable, PageRequest> {

    private final SortDetailsAdaptor sortAdaptor;

    public PageRequestAdaptor(SortDetailsAdaptor sortAdaptor) {
        this.sortAdaptor = sortAdaptor;
    }

    @Override
    public Pageable toSpring(PageRequest pageRequest) {
        if (pageRequest == null) {
            return Pageable.unpaged();
        }

        return org.springframework.data.domain.PageRequest.of(
                pageRequest.pageNumber(),
                pageRequest.pageSize(),
                sortAdaptor.toSpring(pageRequest.sort())
        );
    }

    @Override
    public PageRequest fromSpring(Pageable pageable) {
        if (pageable == null || pageable.isUnpaged()) {
            return PageRequest.unpaged();
        }

        return new DefaultPageRequest(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortAdaptor.fromSpring(pageable.getSort())
        );
    }
}
