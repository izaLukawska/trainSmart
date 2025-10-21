package org.lukawska.trainsmart.statements.application.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementResponse;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserAgreementMapper {

    public static UserAgreementResponse mapToResponse(UserAgreement userAgreement) {
        return new UserAgreementResponse(userAgreement.getId(),
                                         userAgreement.getUser().getId(),
                                         userAgreement.getStatementCode(),
                                         userAgreement.getStatementVersion(),
                                         userAgreement.getStatus());
    }
}
