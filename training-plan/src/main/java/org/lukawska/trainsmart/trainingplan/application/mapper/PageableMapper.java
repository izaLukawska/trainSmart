package org.lukawska.trainsmart.trainingplan.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.trainingplan.model.PagingRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@UtilityClass
public class PageableMapper {

    public static Pageable mapToPageable(PagingRequest pagingRequest) {
        int page = Optional.ofNullable(pagingRequest.getPageNumber()).orElse(0);
        int size = Optional.ofNullable(pagingRequest.getPageSize()).orElse(5);
        Sort.Direction direction = Optional.of(Sort.Direction.valueOf(pagingRequest.getDirection().name()))
                                           .orElse(Sort.Direction.DESC);
        Sort sort = Optional.ofNullable(pagingRequest.getSortBy())
                            .map(sortBy -> Sort.by(direction, pagingRequest.getSortBy().getValue()))
                            .orElse(Sort.unsorted());

        return PageRequest.of(page, size, sort);
    }
}
