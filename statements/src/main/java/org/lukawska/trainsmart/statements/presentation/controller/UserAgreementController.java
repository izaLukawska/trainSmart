package org.lukawska.trainsmart.statements.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.statements.api.UserAgreementApi;
import org.lukawska.trainsmart.statements.application.services.UserAgreementService;
import org.lukawska.trainsmart.statements.model.UserAgreementRequest;
import org.lukawska.trainsmart.statements.model.UserAgreementResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserAgreementController implements UserAgreementApi {

    private final UserAgreementService service;

    @Override
    public ResponseEntity<List<UserAgreementResponse>> getRequiredStatementsToSign(
            Long userId) {
        log.debug("Received get required statements request for user with ID: {}", userId);
        return ResponseEntity.ok(service.getRequiredStatementsToSign(userId));
    }

    @Override
    public ResponseEntity<UserAgreementResponse> signAgreement(Long userId, UserAgreementRequest request) {
        log.info("Received sign statement request for user {} and statement code {}",
                 userId, request.getStatementCode());
        return ResponseEntity.ok(service.signAgreement(userId, request));
    }
}
