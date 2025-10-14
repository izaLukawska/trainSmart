package org.lukawska.trainSmart.mailing.presentation.exception;

import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainSmart.mailing.application.exception.RestException;
import org.lukawska.trainSmart.mailing.presentation.dto.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(RestException.class)
	public ResponseEntity<ExceptionResponse> handleRestException(RestException ex) {
		log.error("Handled RestException: {}", ex.getMessage(), ex);
		return ResponseEntity.status(ex.getExceptionType().getHttpStatus())
		                     .body(new ExceptionResponse(ex.getMessage(),
		                                                 ex.getExceptionType().getHttpStatus().value()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> handleGenericException(Exception ex) {
		log.error("Unexpected error: {}", ex.getMessage(), ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                     .body(new ExceptionResponse(ex.getMessage(),
		                                                 HttpStatus.INTERNAL_SERVER_ERROR.value()));
	}
}
