package org.lukawska.trainSmart.mailing.presentation.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.lukawska.trainSmart.mailing.application.service.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
@Slf4j
public class MailController {

	private final MailService mailService;

	@PostMapping
	public ResponseEntity<Void> sendMail(@Valid @RequestBody MailRequest mailRequest) throws MessagingException {
		log.info("Sending mail to: {}", Arrays.toString(mailRequest.getTo()));
		mailService.sendEmail(mailRequest);
		return ResponseEntity.ok().build();
	}
}
