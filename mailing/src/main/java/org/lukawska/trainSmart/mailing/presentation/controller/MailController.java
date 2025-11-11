package org.lukawska.trainSmart.mailing.presentation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
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
        log.info("Sending mail.");
        return ResponseEntity.status(201).body(mailService.sendMail(mailRequest));
    }

    @GetMapping("/{id}")
    public MailResponse getMailById(@PathVariable @Positive Long id) {
        log.info("Searching for mail with id: {}", id);
        return mailService.getMailResponseById(id);
    }

    @GetMapping("/recipient")
    public List<MailResponse> getAllMailsByRecipient(@RequestParam @NotBlank @Email String recipient) {
        log.debug("Fetching mails for recipient: {}", recipient);
        return mailService.getAllMailsByRecipient(recipient);
    }

    @GetMapping("/subject")
    public List<MailResponse> getAllMailsBySubjectContaining(@RequestParam @NotBlank String keyword) {
        log.debug("Fetching mails with subject containing: {}", keyword);
        return mailService.getAllMailsBySubjectContaining(keyword);
    }
}
