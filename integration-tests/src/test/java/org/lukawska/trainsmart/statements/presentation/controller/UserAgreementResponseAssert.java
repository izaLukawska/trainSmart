package org.lukawska.trainsmart.statements.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementResponse;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class UserAgreementResponseAssert {

    private final UserAgreementResponse userAgreementResponse;

    public static UserAgreementResponseAssert then(UserAgreementResponse userAgreementResponse) {
        return new UserAgreementResponseAssert(userAgreementResponse);
    }

    public UserAgreementResponseAssert hasPositiveId() {
        assertThat(userAgreementResponse.id()).isPositive();
        return this;
    }

    public UserAgreementResponseAssert hasId(Long id) {
        assertThat(userAgreementResponse.id()).isEqualTo(id);
        return this;
    }

    public UserAgreementResponseAssert hasStatementCode(String statementCode) {
        assertThat(userAgreementResponse.statementCode()).isEqualTo(statementCode);
        return this;
    }

    public UserAgreementResponseAssert hasStatus(AgreementStatus agreementStatus) {
        assertThat(userAgreementResponse.agreementStatus()).isEqualTo(agreementStatus);
        return this;
    }

    public UserAgreementResponseAssert hasVersion(int version) {
        assertThat(userAgreementResponse.version()).isEqualTo(version);
        return this;
    }
}
