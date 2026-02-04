package org.lukawska.trainsmart.fileexport.application.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ExceptionType {

    INVALID_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Invalid media type."),
    FILE_GENERATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Error during file processing");

    private final HttpStatus httpStatus;

    private final String message;

}
