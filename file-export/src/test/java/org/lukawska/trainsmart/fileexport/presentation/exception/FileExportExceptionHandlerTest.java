package org.lukawska.trainsmart.fileexport.presentation.exception;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.fileexport.application.exception.ExceptionType;
import org.lukawska.trainsmart.fileexport.application.exception.FileExportException;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

class FileExportExceptionHandlerTest {

    private final FileExportExceptionHandler exceptionHandler = new FileExportExceptionHandler();

    @Test
    void shouldHandleFileExportException() {
        //given
        final FileExportException exception = new FileExportException(ExceptionType.FILE_GENERATION_ERROR);

        //when
        ProblemDetail problemDetail = exceptionHandler.handleFileExportException(exception);

        //then
        assertThat(problemDetail.getStatus()).isEqualTo(exception.getExceptionType().getHttpStatus().value());
        assertThat(problemDetail.getDetail()).isEqualTo(exception.getMessage());
    }
}
