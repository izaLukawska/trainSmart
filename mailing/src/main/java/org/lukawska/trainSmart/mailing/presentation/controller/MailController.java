package org.lukawska.trainSmart.mailing.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.lukawska.trainSmart.mailing.application.dto.MailResponse;
import org.lukawska.trainSmart.mailing.application.service.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
@Slf4j
@Validated
public class MailController {

    private final MailService mailService;

    @PostMapping("/send")
    public ResponseEntity<MailResponse> sendMail(@Valid @RequestBody MailRequest mailRequest) {
        log.debug("Sending mail with subject: {}", mailRequest.subject());
        return ResponseEntity.status(201).body(mailService.sendMail(mailRequest));
    }

    @GetMapping("/{id}")
    public MailResponse getMailById(@PathVariable Long id) {
        log.debug("Searching for mail with id: {}", id);
        return mailService.getMailResponseById(id);
    }

    @GetMapping("/all")
    public List<MailResponse> getAllMails() {
        log.info("Fetching all mails");
        return mailService.getAllMails();
    }

    @GetMapping("/recipient")
    public List<MailResponse> getAllMailsByRecipient(@RequestParam String recipient) {
        log.debug("Fetching mails for recipient: {}", recipient);
        return mailService.getAllMailsByRecipient(recipient);
    }

    @GetMapping("/subject")
    public List<MailResponse> getAllMailsBySubjectContaining(@RequestParam String keyword) {
        log.debug("Fetching mails with subject containing: {}", keyword);
        return mailService.getAllMailsBySubjectContaining(keyword);
    }
}
