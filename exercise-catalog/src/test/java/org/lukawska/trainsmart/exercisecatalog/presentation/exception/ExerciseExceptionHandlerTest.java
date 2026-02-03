package org.lukawska.trainsmart.exercisecatalog.presentation.exception;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.exercisecatalog.application.exception.ExceptionType;
import org.lukawska.trainsmart.exercisecatalog.application.exception.ExerciseException;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

class ExerciseExceptionHandlerTest {

    private final ExerciseExceptionHandler exceptionHandler = new ExerciseExceptionHandler();

    @Test
    void shouldHandleExerciseExceptionWhenExerciseNotFound() {
        //given
        final ExceptionType exceptionType = ExceptionType.EXERCISE_NOT_FOUND;
        final ExerciseException exception = new ExerciseException(exceptionType);

        //when
        ProblemDetail result = exceptionHandler.handleHealthSurveyException(exception);

        //then
        assertThat(result.getTitle()).isEqualTo("Exercise exception");
        assertThat(result.getStatus()).isEqualTo(exceptionType.getHttpStatus().value());
        assertThat(result.getDetail()).isEqualTo(exception.getMessage());
    }

    private static ConstraintViolation<?> mockViolation(String message) {
        Path path = mock(Path.class);
        doReturn(UUID.randomUUID().toString()).when(path).toString();
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn(message);

        return violation;
    }
}
