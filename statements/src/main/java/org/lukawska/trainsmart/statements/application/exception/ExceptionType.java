package org.lukawska.trainsmart.statements.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionType {

	INVALID_STATUS("Status cannot be null.", HttpStatus.BAD_REQUEST),
	INTERNAL_SERVER_ERROR("Server error", HttpStatus.INTERNAL_SERVER_ERROR),
	USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND),
	INVALID_USER_ID("User ID not valid", HttpStatus.BAD_REQUEST),
	STATEMENT_ACCEPTANCE_REQUIRED("Statement acceptance required", HttpStatus.NOT_ACCEPTABLE),
	STATEMENT_NOT_FOUND("Statement not found", HttpStatus.NOT_FOUND);

	private final String message;
	private final HttpStatus status;

}
