package org.lukawska.trainsmart.trainingplan.application.preparation.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanRequest;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lukawska.trainsmart.trainingplan.testutil.TrainingPlanTestData.trainingPlanRequest;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsistentPlanValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @InjectMocks
    private ConsistentPlanValidator validator;

    @ParameterizedTest
    @NullSource
    void shouldReturnTrueWhenRequestIsNull(TrainingPlanRequest request) {
        // when
        boolean result = validator.isValid(request, context);

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    void shouldReturnTrueWhenPreferredDaysIsNullOrEmpty(List<WeekDay> preferredDays) {
        // given
        final TrainingPlanRequest request = new TrainingPlanRequest(
                TrainingType.STRENGTH, PlanDuration.FOUR_WEEKS, 2, preferredDays);

        // when
        boolean result = validator.isValid(request, context);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueWhenCountsMatch() {
        //given
        final TrainingPlanRequest request = trainingPlanRequest();
        //when
        boolean result = validator.isValid(request, context);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseAndAddViolationWhenCountsMismatch() {
        //given
        final TrainingPlanRequest request = new TrainingPlanRequest(
                TrainingType.STRENGTH, PlanDuration.FOUR_WEEKS, 2, List.of(WeekDay.MONDAY));

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        //when
        boolean result = validator.isValid(request, context);

        //then
        assertThat(result).isFalse();
    }
}
