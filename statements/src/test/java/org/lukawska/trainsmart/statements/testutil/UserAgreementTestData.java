package org.lukawska.trainsmart.statements.testutil;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementResponse;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;

import java.util.Random;
import java.util.UUID;

import static org.mockito.Mockito.mock;

@UtilityClass
public class UserAgreementTestData {

    public static UserAgreementRequest acceptedUserAgreementRequest() {
        return new UserAgreementRequest(new Random().nextLong(),
                                        UUID.randomUUID().toString(),
                                        AgreementStatus.ACCEPTED);
    }

    public static UserAgreement acceptedUserAgreement(String statementCode, int version) {
        return new UserAgreement(mock(User.class), statementCode, version, AgreementStatus.ACCEPTED);
    }

    public static UserAgreementResponse userAgreementResponse(Long userId, String statementCode) {
        return new UserAgreementResponse(null, userId, statementCode, 2, AgreementStatus.ACCEPTED);
    }
}
