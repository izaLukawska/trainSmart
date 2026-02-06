package org.lukawska.trainsmart.statements.application.dto;

import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;

public record UserAgreementCommand(String statementCode, AgreementStatus agreementStatus) {}
