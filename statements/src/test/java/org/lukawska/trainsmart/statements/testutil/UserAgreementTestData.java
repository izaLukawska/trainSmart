package org.lukawska.trainsmart.statements.testutil;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;
import org.lukawska.trainsmart.statements.model.AgreementStatusEnum;
import org.lukawska.trainsmart.statements.model.UserAgreementRequest;

import static org.lukawska.trainsmart.statements.testutil.StatementTestData.randomStatementCode;
import static org.mockito.Mockito.mock;

@UtilityClass
public class UserAgreementTestData {

    public static UserAgreementRequest acceptedUserAgreementRequest() {
        return new UserAgreementRequest(randomStatementCode(), AgreementStatusEnum.ACCEPTED);
    }

    public static UserAgreementRequest rejectedUserAgreementRequest(String statementCode) {
        return new UserAgreementRequest(statementCode, AgreementStatusEnum.REJECTED);
    }

    public static UserAgreement acceptedUserAgreement(String statementCode, int version) {
        return new UserAgreement(mock(User.class), statementCode, version, AgreementStatus.ACCEPTED);
    }
}
