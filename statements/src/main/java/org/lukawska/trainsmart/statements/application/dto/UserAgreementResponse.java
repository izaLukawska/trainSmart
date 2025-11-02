package org.lukawska.trainsmart.statements.application.dto;

import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;

public record UserAgreementResponse(Long id,
                                    Long userId,
                                    String statementCode,
                                    int version,
                                    AgreementStatus agreementStatus) {}
