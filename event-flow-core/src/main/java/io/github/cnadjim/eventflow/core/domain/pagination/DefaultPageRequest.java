package io.github.cnadjim.eventflow.core.domain.pagination;

public record DefaultPageRequest(int pageNumber, int pageSize, SortDetails sort) implements PageRequest {

}
