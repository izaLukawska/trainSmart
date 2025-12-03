package org.lukawska.trainsmart.statements.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementResponse;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;

@UtilityClass
public final class UserAgreementMapper {

    public static UserAgreementResponse mapToResponse(UserAgreement userAgreement) {
        return new UserAgreementResponse(userAgreement.getId(),
                                         userAgreement.getStatementCode(),
                                         userAgreement.getStatementVersion(),
                                         userAgreement.getStatus());
    }
}
