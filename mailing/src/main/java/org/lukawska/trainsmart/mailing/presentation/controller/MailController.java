package org.lukawska.trainsmart.mailing.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.mailing.api.MailApi;
import org.lukawska.trainsmart.mailing.application.service.MailService;
import org.lukawska.trainsmart.mailing.model.MailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class MailController implements MailApi {

    private final MailService mailService;

    @Override
    public ResponseEntity<List<MailResponse>> getAllMailsByRecipient(String recipient) {
        log.info("Received get all email by recipient request");
        return ResponseEntity.ok(mailService.getAllMailsByRecipient(recipient));
    }

    @Override
    public ResponseEntity<List<MailResponse>> getAllMailsBySubjectContaining(String keyword) {
        log.info("Received get all email by keyword request");
        return ResponseEntity.ok(mailService.getAllMailsBySubjectContaining(keyword));
    }

    @Override
    public ResponseEntity<MailResponse> getMailById(Long id) {
        log.info("Received get all email by id request");
        return ResponseEntity.ok(mailService.getMailResponseById(id));
    }
}
