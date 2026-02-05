package org.lukawska.trainsmart.trainingplan.application.mapper;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.trainingplan.model.PagingRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

class PageableMapperTest {

    @Test
    void shouldMapAllPagingFields() {
        //given
        final PagingRequest pagingRequest = PagingRequest.builder()
                                                         .pageNumber(2)
                                                         .pageSize(10)
                                                         .direction(PagingRequest.DirectionEnum.DESC)
                                                         .sortBy(PagingRequest.SortByEnum.ID)
                                                         .build();

        //when
        Pageable pageable = PageableMapper.mapToPageable(pagingRequest);

        //then
        assertThat(pageable.getPageNumber()).isEqualTo(pagingRequest.getPageNumber());
        assertThat(pageable.getPageSize()).isEqualTo(pagingRequest.getPageSize());
        assertThat(pageable.getSort().isSorted()).isTrue();
        assertThat(pageable.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, PagingRequest.SortByEnum.ID.getValue()));
    }

    @Test
    void shouldUseDefaultValuesWhenNull() {
        // given
        final PagingRequest pagingRequest = PagingRequest.builder().build();

        // when
        Pageable pageable = PageableMapper.mapToPageable(pagingRequest);

        // then
        assertThat(pageable.getPageNumber()).isEqualTo(pagingRequest.getPageNumber());
        assertThat(pageable.getPageSize()).isEqualTo(pagingRequest.getPageSize());
        assertThat(pageable.getSort().isUnsorted()).isTrue();
    }
}
