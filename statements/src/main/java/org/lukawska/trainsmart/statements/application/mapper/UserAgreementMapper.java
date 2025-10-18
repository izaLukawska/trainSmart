package org.lukawska.trainsmart.statements.application.mapper;

import org.lukawska.trainsmart.statements.application.dto.UserAgreementResponse;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.springframework.stereotype.Component;

@Component
public class UserAgreementMapper {

	public UserAgreementResponse mapToResponse(UserAgreement userAgreement) {
		return new UserAgreementResponse(userAgreement.getId(),
		                                 userAgreement.getUserId(),
		                                 userAgreement.getStatementCode(),
		                                 userAgreement.getStatementVersion(),
		                                 userAgreement.getStatus());
	}
}
