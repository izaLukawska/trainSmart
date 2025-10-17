package org.lukawska.trainsmart.statements.presentation.exception;

import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.statements.application.exception.RestException;
import org.lukawska.trainsmart.statements.presentation.dto.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalRestExceptionHandler {

	@ExceptionHandler(RestException.class)
	public ResponseEntity<ExceptionResponse> handleRestException(RestException exception) {
		log.error("Error with status: {}  and message: {} occured",
		          exception.getExceptionType().getStatus(), exception.getMessage());

		return ResponseEntity
				.status(exception.getExceptionType().getStatus())
				.body(new ExceptionResponse(
						      exception.getMessage(),
						      exception.getExceptionType().getStatus().value())
				     );
	}
}
