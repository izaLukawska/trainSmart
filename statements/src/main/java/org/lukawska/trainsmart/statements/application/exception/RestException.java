package org.lukawska.trainsmart.statements.application.exception;

import lombok.Getter;

@Getter
public class RestException extends RuntimeException {

	private final ExceptionType exceptionType;

	public RestException(ExceptionType exceptionType) {
		super(exceptionType.getMessage());
		this.exceptionType = exceptionType;
	}
}
