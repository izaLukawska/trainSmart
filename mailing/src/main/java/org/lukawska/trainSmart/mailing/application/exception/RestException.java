package org.lukawska.trainSmart.mailing.application.exception;

import lombok.Getter;

@Getter
public class RestException extends RuntimeException {

	private final ExceptionType exceptionType;

	public RestException(ExceptionType exceptionType) {
		super(exceptionType.getMessage());
		this.exceptionType = exceptionType;
	}
}
