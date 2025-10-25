package org.lukawska.trainSmart.mailing.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.lukawska.trainSmart.mailing.application.dto.MailResponse;
import org.lukawska.trainSmart.mailing.application.service.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
        MailResponse response = mailService.sendMail(mailRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                  .path("/{id}")
                                                  .buildAndExpand(response.id())
                                                  .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public MailResponse getMailById(@PathVariable Long id) {
        log.debug("Searching for mail with id: {}", id);
        return mailService.getMailResponseById(id);
    }

    @GetMapping
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
