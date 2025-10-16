package org.lukawska.trainsmart.statements.application.exception;

public class StatementAcceptanceRequired extends RuntimeException {
	public StatementAcceptanceRequired(String message) {
		super(message);
	}
}
