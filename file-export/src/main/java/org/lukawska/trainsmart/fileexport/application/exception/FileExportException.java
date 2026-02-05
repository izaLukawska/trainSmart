package org.lukawska.trainsmart.fileexport.application.exception;

import lombok.Getter;

@Getter
public class FileExportException extends RuntimeException {

    private final ExceptionType exceptionType;

    public FileExportException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }
}
