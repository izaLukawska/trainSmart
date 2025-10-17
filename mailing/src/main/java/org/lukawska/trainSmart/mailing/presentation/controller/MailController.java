package org.lukawska.trainSmart.mailing.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.lukawska.trainSmart.mailing.application.dto.MailResponse;
import org.lukawska.trainSmart.mailing.application.service.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
@Slf4j
public class MailController {

	private final MailService mailService;

	@PostMapping("/send")
	public ResponseEntity<MailResponse> sendMail(@Valid @RequestBody MailRequest mailRequest) {
		log.debug("Sending mail with subject: {}", mailRequest.subject());
		return ResponseEntity.ok(mailService.sendMail(mailRequest));
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<MailResponse> getMailById(@PathVariable Long id) {
		log.info("Searching for mail with id: {}", id);
		return ResponseEntity.ok(mailService.getMailResponseById(id));
	}

	@GetMapping
	public ResponseEntity<List<MailResponse>> getAllMails() {
		log.info("Fetching all mails");
		return ResponseEntity.ok(mailService.getAllMails());
	}

	@GetMapping("/recipient")
	public ResponseEntity<List<MailResponse>> getAllMailsByRecipient(@RequestParam String recipient) {
		log.debug("Fetching mails for recipient: {}", recipient);
		return ResponseEntity.ok(mailService.getAllMailsByRecipient(recipient));
	}

	@GetMapping("/subject")
	public ResponseEntity<List<MailResponse>> getAllMailsBySubjectContaining(@RequestParam String keyword) {
		log.info("Fetching mails with subject containing: {}", keyword);
		return ResponseEntity.ok(mailService.getAllMailsBySubjectContaining(keyword));
	}
}
