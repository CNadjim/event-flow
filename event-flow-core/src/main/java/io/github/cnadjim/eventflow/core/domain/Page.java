package io.github.cnadjim.eventflow.core.domain;

import java.util.List;

public record Page<R>(List<R> content,
                      long totalItems,
                      int totalPages,
                      int pageNumber,
                      int pageSize) {

}
