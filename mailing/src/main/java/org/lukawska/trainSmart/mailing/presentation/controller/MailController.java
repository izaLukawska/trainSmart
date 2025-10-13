package org.lukawska.trainSmart.mailing.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.lukawska.trainSmart.mailing.application.dto.MailResponse;
import org.lukawska.trainSmart.mailing.application.service.MailService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
@Slf4j
public class MailController {

	private final MailService mailService;

	public MailResponse sendMail(@Valid @RequestBody MailRequest mailRequest) {
		log.debug("Received mail with subject: {}", mailRequest.subject());
		return mailService.sendMail(mailRequest);
	}
}
