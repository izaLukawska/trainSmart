package org.lukawska.trainsmart.statements.application.dto;

import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;

public record UserAgreementResponse(Long id, String statementCode, int version, AgreementStatus agreementStatus) {}
