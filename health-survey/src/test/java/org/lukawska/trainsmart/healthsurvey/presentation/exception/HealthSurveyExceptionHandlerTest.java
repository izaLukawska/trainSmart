package org.lukawska.trainsmart.healthsurvey.presentation.exception;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.healthsurvey.application.exception.ExceptionType;
import org.lukawska.trainsmart.healthsurvey.application.exception.HealthSurveyException;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

class HealthSurveyExceptionHandlerTest {

    private final HealthSurveyExceptionHandler exceptionHandler = new HealthSurveyExceptionHandler();

    @Test
    void shouldHandleHealthSurveyExceptionWhenHealthSurveyNotFound() {
        //given
        final ExceptionType exceptionType = ExceptionType.HEALTH_SURVEY_NOT_FOUND;
        final HealthSurveyException exception = new HealthSurveyException(exceptionType);

        //when
        ProblemDetail result = exceptionHandler.handleHealthSurveyException(exception);

        //then
        assertThat(result.getTitle()).isEqualTo("Health survey exception");
        assertThat(result.getStatus()).isEqualTo(exceptionType.getHttpStatus().value());
        assertThat(result.getDetail()).isEqualTo(exception.getMessage());
    }
}
