package org.lukawska.trainsmart.mailing.presentation.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.mailing.application.dto.MailResponse;
import org.lukawska.trainsmart.mailing.application.service.MailService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
@Slf4j
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class MailController {

    private final MailService mailService;

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
