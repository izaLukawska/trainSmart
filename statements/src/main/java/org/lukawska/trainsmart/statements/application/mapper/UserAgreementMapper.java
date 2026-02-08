package org.lukawska.trainsmart.statements.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementCommand;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;
import org.lukawska.trainsmart.statements.model.AgreementStatusEnum;
import org.lukawska.trainsmart.statements.model.UserAgreementRequest;
import org.lukawska.trainsmart.statements.model.UserAgreementResponse;

@UtilityClass
public final class UserAgreementMapper {

    public static UserAgreementResponse mapToResponse(UserAgreement userAgreement) {
        return new UserAgreementResponse(userAgreement.getId(),
                                         userAgreement.getStatementCode(),
                                         userAgreement.getStatementVersion(),
                                         AgreementStatusEnum.valueOf(userAgreement.getStatus().name()));
    }

    public static UserAgreementCommand mapToCommand(UserAgreementRequest userAgreementRequest) {
        return new UserAgreementCommand(userAgreementRequest.getStatementCode(),
                                        AgreementStatus.valueOf(userAgreementRequest.getAgreementStatus().name()));
    }
}
