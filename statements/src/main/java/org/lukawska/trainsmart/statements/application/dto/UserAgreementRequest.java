package org.lukawska.trainsmart.statements.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;

public record UserAgreementRequest(@NotNull Long userId,
                                   @NotBlank String statementCode,
                                   @NotNull AgreementStatus agreementStatus) {}
