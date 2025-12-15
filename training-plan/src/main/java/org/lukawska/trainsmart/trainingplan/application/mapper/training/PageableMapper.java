package org.lukawska.trainsmart.trainingplan.application.mapper.training;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.lukawska.trainsmart.trainingplan.application.dto.request.PagingRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@UtilityClass
public class PageableMapper {

    public static Pageable mapToPageable(PagingRequest request) {
        if (request == null) {
            return PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "id"));
        }

        int pageNumber = Optional.ofNullable(request.pageNumber()).orElse(0);
        int pageSize = Optional.ofNullable(request.pageSize()).orElse(5);
        Sort.Direction direction = Optional.ofNullable(request.sortDirection()).orElse(Sort.Direction.ASC);
        String sortBy = StringUtils.isBlank(request.sortBy()) ? "id" : request.sortBy();

        return PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
    }
}
