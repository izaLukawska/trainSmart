package org.lukawska.trainsmart.trainingplan.application.dto.request;

import lombok.Builder;
import org.springframework.data.domain.Sort;

@Builder
public record PagingRequest(Integer pageNumber, Integer pageSize, String sortBy, Sort.Direction direction) {}
