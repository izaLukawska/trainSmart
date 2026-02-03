package org.lukawska.trainsmart.statements.presentation.exception;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.statements.application.exception.ExceptionType;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

class StatementsExceptionHandlerTest {

    private final StatementsExceptionHandler exceptionHandler = new StatementsExceptionHandler();

    @Test
    void shouldHandleStatementExceptionWhenStatementNotFound() {
        //given
        final ExceptionType exceptionType = ExceptionType.STATEMENT_NOT_FOUND;
        final StatementException statementException = new StatementException(exceptionType);

        //when
        ProblemDetail result = exceptionHandler.handleStatementException(statementException);

        //then
        assertThat(result.getDetail()).isEqualTo(statementException.getMessage());
        assertThat(result.getTitle()).isEqualTo("Statement exception");
        assertThat(result.getStatus()).isEqualTo(exceptionType.getStatus().value());
    }
}
