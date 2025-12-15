package org.lukawska.trainsmart.trainingplan.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Sort;

@Builder
@Getter
public class PagingRequest {

    @Builder.Default
    private int pageNumber = 0;

    @Builder.Default
    private int pageSize = 1;

    @Builder.Default
    private String sortBy = "createdAt";

    @Builder.Default
    private Sort.Direction direction = Sort.Direction.ASC;

}
