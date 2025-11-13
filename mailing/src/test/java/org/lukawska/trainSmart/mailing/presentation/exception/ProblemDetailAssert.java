package org.lukawska.trainSmart.mailing.presentation.exception;

import jakarta.validation.ConstraintViolation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProblemDetailAssert {

    private final ProblemDetail problemDetail;

    public static ProblemDetailAssert then(ProblemDetail problemDetail) {
        return new ProblemDetailAssert(problemDetail);
    }

    public ProblemDetailAssert isNotNull() {
        assertThat(problemDetail).isNotNull();
        return this;
    }

    public ProblemDetailAssert hasTitle(String title) {
        assertThat(problemDetail.getTitle()).isEqualTo(title);
        return this;
    }

    public ProblemDetailAssert hasStatus(HttpStatus httpStatus) {
        assertThat(problemDetail.getStatus()).isEqualTo(httpStatus.value());
        return this;
    }

    public ProblemDetailAssert hasDetail(String detail) {
        assertThat(problemDetail.getDetail()).isEqualTo(detail);
        return this;
    }

    public ProblemDetailAssert hasFieldErrorProperty(FieldError fieldError) {
        assertThat(problemDetail.getProperties()).containsEntry(fieldError.getField(),
                                                                fieldError.getDefaultMessage());
        return this;
    }

    public ProblemDetailAssert hasConstraintViolation(ConstraintViolation<?> violation) {
        assertThat(problemDetail.getProperties()).containsEntry(violation.getPropertyPath().toString(),
                                                                violation.getMessage());
        return this;
    }
}
