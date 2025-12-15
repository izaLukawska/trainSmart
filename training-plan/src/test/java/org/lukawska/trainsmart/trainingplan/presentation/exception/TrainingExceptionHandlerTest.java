package org.lukawska.trainsmart.trainingplan.presentation.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.trainingplan.application.exception.ExceptionType;
import org.lukawska.trainsmart.trainingplan.application.exception.TrainingPlanException;
import org.lukawska.trainsmart.trainingplan.application.exception.UserExerciseAlreadyExistsException;
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
import static org.mockito.Mockito.*;

class TrainingExceptionHandlerTest {

    private final TrainingExceptionHandler exceptionHandler = new TrainingExceptionHandler();

    static ConstraintViolation<?> mockViolation(String message) {
        Path path = mock(Path.class);
        doReturn(UUID.randomUUID().toString()).when(path).toString();
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn(message);

        return violation;
    }

    @Test
    void shouldReturn404WithProblemDetailWhenHandleTrainingException() {
        //given
        final TrainingPlanException exception = new TrainingPlanException(ExceptionType.TRAINING_PLAN_NOT_FOUND);

        //when
        ProblemDetail result = exceptionHandler.handleTrainingPlanException(exception);

        //then
        ProblemDetailAssert.then(result)
                           .hasStatus(HttpStatus.NOT_FOUND)
                           .hasDetail(exception.getMessage())
                           .hasTitle("Training plan exception");
    }

    @Test
    void shouldReturn404WithProblemDetailWhenHandleUserExerciseException() {
        //given
        final UserExerciseAlreadyExistsException exception = new UserExerciseAlreadyExistsException(1L);

        //when
        ProblemDetail result = exceptionHandler.handleUserExerciseException(exception);

        //then
        ProblemDetailAssert.then(result)
                           .hasStatus(HttpStatus.CONFLICT)
                           .hasDetail(exception.getMessage())
                           .hasTitle("UserExercise exception");
    }

    @Test
    void shouldReturn400WithProblemDetailWhenMethodArgumentNotValidException() {
        // given
        final FieldError fieldError1 = new FieldError("object", "muscle group", "wrong muscle group");
        final FieldError fieldError2 = new FieldError("object", "exercise", "must not be blank");

        final BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        final MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(ex.getMessage()).thenReturn("Validation failed");

        // when
        ResponseEntity<Object> response = exceptionHandler.handleMethodArgumentNotValid(
                ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, mock(WebRequest.class));

        // then
        assertThat(response).isNotNull();
        ProblemDetail problemDetail = (ProblemDetail) response.getBody();
        ProblemDetailAssert.then(problemDetail)
                           .isNotNull()
                           .hasTitle("Validation failure")
                           .hasStatus(HttpStatus.BAD_REQUEST)
                           .hasFieldErrorProperty(fieldError1)
                           .hasFieldErrorProperty(fieldError2);
    }

    @Test
    void shouldReturn400WithProblemDetailWhenConstraintViolation() {
        //given
        final ConstraintViolation<?> violation1 = mockViolation(UUID.randomUUID().toString());
        final ConstraintViolation<?> violation2 = mockViolation(UUID.randomUUID().toString());
        final ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation1, violation2));

        //when
        ProblemDetail result = exceptionHandler.handleConstraintViolation(ex);

        //then
        ProblemDetailAssert.then(result)
                           .hasStatus(HttpStatus.BAD_REQUEST)
                           .hasTitle("Constraint violation")
                           .hasConstraintViolation(violation1)
                           .hasConstraintViolation(violation2);
    }
}
