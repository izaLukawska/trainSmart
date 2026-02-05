package org.lukawska.trainsmart.fileexport.presentation.exception;

import org.lukawska.trainsmart.fileexport.application.exception.FileExportException;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FileExportExceptionHandler {

    @ExceptionHandler(FileExportException.class)
    public ProblemDetail handleFileExportException(FileExportException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getExceptionType().getHttpStatus(),
                                                                       ex.getMessage());
        problemDetail.setTitle("File export exception");
        return problemDetail;
    }
}
