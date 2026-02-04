package org.lukawska.trainsmart.trainingplan.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.trainingplan.model.PagingRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@UtilityClass
public class PageableMapper {

    private static final Integer BASE_PAGE_NUMBER = 0;

    private static final Integer BASE_PAGE_SIZE = 5;

    public static Pageable mapToPageable(PagingRequest pagingRequest) {
        int page = Optional.ofNullable(pagingRequest.getPageNumber()).orElse(BASE_PAGE_NUMBER);
        int size = Optional.ofNullable(pagingRequest.getPageSize()).orElse(BASE_PAGE_SIZE);
        Sort.Direction direction = Optional.of(Sort.Direction.valueOf(pagingRequest.getDirection().name()))
                                           .orElse(Sort.Direction.DESC);
        Sort sort = Optional.ofNullable(pagingRequest.getSortBy())
                            .map(sortBy -> Sort.by(direction, pagingRequest.getSortBy().getValue()))
                            .orElse(Sort.unsorted());

        return PageRequest.of(page, size, sort);
    }
}
