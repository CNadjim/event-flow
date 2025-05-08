package io.github.cnadjim.eventflow.spring.starter.adaptor;

import io.github.cnadjim.eventflow.core.domain.pagination.DefaultPage;
import io.github.cnadjim.eventflow.core.domain.pagination.Page;
import io.github.cnadjim.eventflow.core.domain.pagination.PageDetails;
import io.github.cnadjim.eventflow.core.domain.pagination.SortDetails;

import java.util.List;
import java.util.function.Function;

import static java.util.Objects.isNull;

public class PageAdaptor implements SpringPageAdaptor {

    private final SortDetailsAdaptor sortAdaptor;
    private final PageDetailsAdaptor pageDetailsAdaptor;

    public PageAdaptor(SortDetailsAdaptor sortAdaptor, PageDetailsAdaptor pageDetailsAdaptor) {
        this.sortAdaptor = sortAdaptor;
        this.pageDetailsAdaptor = pageDetailsAdaptor;
    }

    @Override
    public <SPRING_PAGE_TYPE, EVENT_FLOW_PAGE_TYPE> Page<EVENT_FLOW_PAGE_TYPE> fromSpring(org.springframework.data.domain.Page<SPRING_PAGE_TYPE> springPage, Function<SPRING_PAGE_TYPE, EVENT_FLOW_PAGE_TYPE> mapping) {
        if (isNull(springPage) || isNull(mapping)) {
            return Page.empty();
        }

        final PageDetails pageDetails = pageDetailsAdaptor.fromSpring(springPage);

        final SortDetails sortDetails = sortAdaptor.fromSpring(springPage.getSort());

        final List<EVENT_FLOW_PAGE_TYPE> pageContent = springPage.getContent().stream().map(mapping).toList();

        return new DefaultPage<>(pageDetails, sortDetails, pageContent);
    }
}
