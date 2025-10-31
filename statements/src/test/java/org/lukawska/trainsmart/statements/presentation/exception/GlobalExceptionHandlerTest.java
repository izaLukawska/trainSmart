package org.lukawska.trainsmart.statements.presentation.exception;


import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.statements.application.exception.ExceptionType;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void shouldHandleStatementExceptionWhenUserAgreementNotFound() {
        //given
        final StatementException statementException = new StatementException(ExceptionType.STATEMENT_NOT_FOUND);

        //when
        ProblemDetail result = exceptionHandler.handleStatementException(statementException);

        //then
        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(result.getTitle()).isEqualTo("Statement exception");
        assertThat(result.getDetail()).isEqualTo(statementException.getMessage());
        assertThat(result.getType().getPath()).isEqualTo("/errors/statement-not-found");
    }

    @Test
    void shouldHandleConstraintViolation() {
        //given

        //when

        //then
    }

    @Test
    void shouldHandleGenericException() {
        //given

        //when

        //then
    }

    @Test
    void shouldHandleMethodArgumentNotValid() {
        // given
        ProblemType problemType = ProblemType.METHOD_ARGUMENT_NOT_VALID;
        FieldError fieldError1 = new FieldError("object", "username", "must not be blank");
        FieldError fieldError2 = new FieldError("object", "lastname", "must be null");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(mock(MethodParameter.class),
                                                                                 bindingResult);

        // when
        ProblemDetail result = (ProblemDetail)
                Objects.requireNonNull(exceptionHandler.handleMethodArgumentNotValid(ex,
                                                                                     new HttpHeaders(),
                                                                                     HttpStatus.BAD_REQUEST,
                                                                                     mock(WebRequest.class)))
                       .getBody();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(problemType.getStatus().value());
        assertThat(result.getTitle()).isEqualTo(problemType.getTitle());
        assertThat(result.getType().getPath()).isEqualTo(problemType.getTypeUri());
        assertThat(result.getProperties()).hasFieldOrProperty("field errors");

        @SuppressWarnings("unchecked")
        Map<String, String> fieldErrors = Objects.requireNonNull(
                (Map<String, String>) Objects.requireNonNull(result.getProperties()).get("field errors"));

        assertThat(fieldErrors).isNotNull()
                               .hasSize(2)
                               .containsEntry("username", "must not be blank")
                               .containsEntry("lastname", "must be null");
    }
}
