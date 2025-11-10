package org.lukawska.trainsmart.statements.testutil;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementResponse;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;

import java.util.Random;

import static org.lukawska.trainsmart.statements.testutil.StatementTestData.randomStatementCode;
import static org.mockito.Mockito.mock;

@UtilityClass
public class UserAgreementTestData {

    public static UserAgreementRequest acceptedUserAgreementRequest() {
        return new UserAgreementRequest(new Random().nextLong(), randomStatementCode(), AgreementStatus.ACCEPTED);
    }

    public static UserAgreementRequest rejectedUserAgreementRequest(String statementCode) {
        return new UserAgreementRequest(new Random().nextLong(10), statementCode, AgreementStatus.REJECTED);
    }

    public static UserAgreement acceptedUserAgreement(String statementCode, int version) {
        return new UserAgreement(mock(User.class), statementCode, version, AgreementStatus.ACCEPTED);
    }

    public static UserAgreementResponse userAgreementResponse(Long userId, String statementCode) {
        return new UserAgreementResponse(null, userId, statementCode, 2, AgreementStatus.ACCEPTED);
    }
}
