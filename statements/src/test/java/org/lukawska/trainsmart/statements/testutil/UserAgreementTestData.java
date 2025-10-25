package org.lukawska.trainsmart.statements.testutil;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;

import java.util.Random;
import java.util.UUID;

@UtilityClass
public class UserAgreementTestData {

    public static UserAgreementRequest randomUserAgreementRequest() {
        return new UserAgreementRequest(new Random().nextLong(),
                                        UUID.randomUUID().toString(),
                                        AgreementStatus.ACCEPTED);
    }

    public static UserAgreement specificUserAgreement(User user, String code, int version) {
        return new UserAgreement(user,
                                 code,
                                 version,
                                 AgreementStatus.ACCEPTED);
    }
}
