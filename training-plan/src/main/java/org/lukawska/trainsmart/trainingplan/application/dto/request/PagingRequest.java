package org.lukawska.trainsmart.trainingplan.application.dto.request;

import org.springframework.data.domain.Sort;

public record PagingRequest(Integer pageNumber,
                            Integer pageSize,
                            String sortBy,
                            Sort.Direction sortDirection) {
}
