package org.lukawska.trainsmart.mailing.presentation.exception;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainsmart.mailing.application.exception.MailingException;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

class MailingExceptionHandlerTest {

    private final MailingExceptionHandler exceptionHandler = new MailingExceptionHandler();

    @Test
    void shouldReturn404WithProblemDetailWhenHandleMailingException() {
        //given
        final ExceptionType exceptionType = ExceptionType.MAIL_NOT_FOUND;
        final MailingException exception = new MailingException(exceptionType);

        //when
        ProblemDetail result = exceptionHandler.handleMailingException(exception);

        //then
        assertThat(result.getTitle()).isEqualTo("Mailing exception");
        assertThat(result.getDetail()).isEqualTo(exception.getMessage());
        assertThat(result.getStatus()).isEqualTo(exceptionType.getHttpStatus().value());
    }
}
