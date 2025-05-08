package io.github.cnadjim.eventflow.spring.starter.adaptor;

import io.github.cnadjim.eventflow.core.domain.exception.NotImplementedException;
import io.github.cnadjim.eventflow.core.domain.pagination.DefaultPageDetails;
import io.github.cnadjim.eventflow.core.domain.pagination.PageDetails;

public class PageDetailsAdaptor implements SpringAdaptor<org.springframework.data.domain.Page<?>, PageDetails> {
    @Override
    public org.springframework.data.domain.Page<?> toSpring(PageDetails pageDetails) {
        throw new NotImplementedException("Converting PageDetails to Spring Page is not supported");
    }

    @Override
    public PageDetails fromSpring(org.springframework.data.domain.Page<?> springPage) {
        if (springPage == null) {
            return PageDetails.unPaged();
        }

        return new DefaultPageDetails(
                springPage.getNumber(),
                springPage.getSize(),
                springPage.getTotalElements(),
                springPage.getTotalPages()
        );
    }
}
