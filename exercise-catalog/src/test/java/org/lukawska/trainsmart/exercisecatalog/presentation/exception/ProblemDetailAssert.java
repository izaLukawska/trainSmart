package org.lukawska.trainsmart.exercisecatalog.presentation.exception;

import jakarta.validation.ConstraintViolation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
class ProblemDetailAssert {

    private final ProblemDetail problemDetail;

    static ProblemDetailAssert then(ProblemDetail problemDetail) {
        return new ProblemDetailAssert(problemDetail);
    }

    ProblemDetailAssert isNotNull() {
        assertThat(problemDetail).isNotNull();
        return this;
    }

    ProblemDetailAssert hasTitle(String title) {
        assertThat(problemDetail.getTitle()).isEqualTo(title);
        return this;
    }

    ProblemDetailAssert hasStatus(HttpStatus httpStatus) {
        assertThat(problemDetail.getStatus()).isEqualTo(httpStatus.value());
        return this;
    }

    ProblemDetailAssert hasDetail(String detail) {
        assertThat(problemDetail.getDetail()).isEqualTo(detail);
        return this;
    }

    ProblemDetailAssert hasFieldErrorProperty(FieldError fieldError) {
        assertThat(problemDetail.getProperties()).containsEntry(fieldError.getField(),
                                                                fieldError.getDefaultMessage());
        return this;
    }

    ProblemDetailAssert hasConstraintViolation(ConstraintViolation<?> violation) {
        assertThat(problemDetail.getProperties()).containsEntry(violation.getPropertyPath().toString(),
                                                                violation.getMessage());
        return this;
    }
}
