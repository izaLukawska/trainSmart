package org.lukawska.trainsmart.healthsurvey.presentation.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.healthsurvey.application.exception.ExceptionType;
import org.lukawska.trainsmart.healthsurvey.application.exception.HealthSurveyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lukawska.trainsmart.healthsurvey.testutil.ExceptionTestData.mockViolation;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HealthSurveyExceptionHandlerTest {

    private final HealthSurveyExceptionHandler exceptionHandler = new HealthSurveyExceptionHandler();

    @Test
    void shouldHandleHealthSurveyExceptionWhenHealthSurveyNotFound() {
        //given
        final HealthSurveyException exception = new HealthSurveyException(ExceptionType.HEALTH_SURVEY_NOT_FOUND);

        //when
        ProblemDetail result = exceptionHandler.handleHealthSurveyException(exception);

        //then
        ProblemDetailAssert.then(result)
                           .isNotNull()
                           .hasStatus(HttpStatus.NOT_FOUND)
                           .hasDetail(exception.getMessage())
                           .hasTitle("Health survey exception");
    }

    @Test
    void shouldHandleConstraintViolation() {
        //given
        final ConstraintViolation<?> violation1 = mockViolation(UUID.randomUUID().toString());
        final ConstraintViolation<?> violation2 = mockViolation(UUID.randomUUID().toString());
        final ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation1, violation2));

        //when
        ProblemDetail result = exceptionHandler.handleConstraintViolation(ex);

        //then
        ProblemDetailAssert.then(result)
                           .isNotNull()
                           .hasStatus(HttpStatus.BAD_REQUEST)
                           .hasTitle("Constraint violation")
                           .hasConstraintViolation(violation1)
                           .hasConstraintViolation(violation2);
    }

    @Test
    void shouldHandleMethodArgumentNotValidAndUseDefaultMessageIfFieldErrorMessageNotPresent() {
        // given
        final FieldError fieldError1 = new FieldError("object", "age", "must be 18");
        final FieldError fieldError2 = new FieldError("object", "username", "must not be blank");

        final BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        final MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(ex.getMessage()).thenReturn("Validation failed");

        // when
        ResponseEntity<Object> response = exceptionHandler.handleMethodArgumentNotValid(ex,
                                                                                        new HttpHeaders(),
                                                                                        HttpStatus.BAD_REQUEST,
                                                                                        mock(WebRequest.class));

        // then
        assertThat(response).isNotNull();
        ProblemDetail problemDetail = (ProblemDetail) response.getBody();
        ProblemDetailAssert.then(problemDetail)
                           .isNotNull()
                           .hasStatus(HttpStatus.BAD_REQUEST)
                           .hasTitle("Validation failure")
                           .hasFieldErrorProperty(fieldError1)
                           .hasFieldErrorProperty(fieldError2);
    }
}
