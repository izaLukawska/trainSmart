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
@RequestMapping("/users/{userId}/agreements")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserAgreementController {

    private final UserAgreementService service;

    @PutMapping("/sign")
    public ResponseEntity<UserAgreementResponse> signAgreement(@PathVariable @Positive Long userId,
                                                               @Valid @RequestBody UserAgreementRequest request) {
        log.debug("Signing statement for user with ID: {} and statement code: {}", userId, request.statementCode());
        return ResponseEntity.ok(service.signAgreement(userId, request));
    }

    @GetMapping
    public List<UserAgreementResponse> getRequiredStatementsToSign(@PathVariable @Positive Long userId) {
        log.debug("Retrieving required statements for user with ID: {}", userId);
        return service.getRequiredStatementsToSign(userId);
    }
}
