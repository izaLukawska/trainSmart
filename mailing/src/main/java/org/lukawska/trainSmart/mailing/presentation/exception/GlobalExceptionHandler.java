package org.lukawska.trainSmart.mailing.presentation.exception;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception e) {
		log.error("Unexpected error encountered: ", e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                     .body("Internal server error: " + e.getMessage());
	}

	@ExceptionHandler(MessagingException.class)
	public ResponseEntity<String> handleMessagingException(MessagingException e) {
		log.error("Error during mail sending: ", e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                     .body("Error during mail sending: " + e.getMessage());
	}

	@ExceptionHandler(MailAuthenticationException.class)
	public ResponseEntity<String> handleMailAuthException(MailAuthenticationException e) {
		log.error("Authentication error during mail sending: ", e);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
		                     .body("Mail authentication failed: " + e.getMessage());
	}
}
