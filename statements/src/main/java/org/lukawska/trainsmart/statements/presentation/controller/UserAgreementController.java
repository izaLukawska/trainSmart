package org.lukawska.trainsmart.statements.presentation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementResponse;
import org.lukawska.trainsmart.statements.application.services.UserAgreementService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agreements")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserAgreementController {

    private final UserAgreementService service;

    @PostMapping("/sign")
    public ResponseEntity<UserAgreementResponse> signNewAgreement(@Valid @RequestBody UserAgreementRequest request) {
        log.debug("Signing agreement for user with ID: {} for statement {} with status {}",
                  request.userId(), request.statementCode(), request.status());
        return ResponseEntity.status(201).body(service.signNewAgreement(request));
    }

    @PutMapping("/re-sign")
    public ResponseEntity<UserAgreementResponse> reSignAgreement(@Valid @RequestBody UserAgreementRequest request) {
        log.debug("Updating statement for user with ID: {} and statement code: {}",
                  request.userId(), request.statementCode());
        return ResponseEntity.ok(service.reSignAgreement(request));
    }

    @GetMapping("/{id}")
    public List<UserAgreementResponse> getRequiredStatementsToSign(@PathVariable("id") @Positive Long userId) {
        log.debug("Retrieving required statements for user with ID: {}", userId);
        return service.getRequiredStatementsToSign(userId);
    }
}
