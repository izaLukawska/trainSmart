package org.lukawska.trainsmart.trainingplan.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.trainingplan.application.dto.request.PagingRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@UtilityClass
public class PageableMapper {

    public static Pageable mapToPageable(PagingRequest pagingRequest) {
        int page = Optional.ofNullable(pagingRequest.pageNumber()).orElse(0);
        int size = Optional.ofNullable(pagingRequest.pageSize()).orElse(5);
        Sort.Direction direction = Optional.ofNullable(pagingRequest.direction()).orElse(Sort.Direction.DESC);
        Sort sort = Optional.ofNullable(pagingRequest.sortBy())
                            .map(sortBy -> Sort.by(direction, pagingRequest.sortBy()))
                            .orElse(Sort.unsorted());

        return PageRequest.of(page, size, sort);
    }
}
