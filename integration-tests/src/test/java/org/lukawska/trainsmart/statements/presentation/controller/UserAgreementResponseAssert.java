package org.lukawska.trainsmart.statements.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.statements.model.AgreementStatusEnum;
import org.lukawska.trainsmart.statements.model.UserAgreementResponse;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class UserAgreementResponseAssert {

    private final UserAgreementResponse userAgreementResponse;

    public static UserAgreementResponseAssert then(UserAgreementResponse userAgreementResponse) {
        return new UserAgreementResponseAssert(userAgreementResponse);
    }

    public UserAgreementResponseAssert hasId(Long id) {
        assertThat(userAgreementResponse.getId()).isEqualTo(id);
        return this;
    }

    public UserAgreementResponseAssert hasStatementCode(String statementCode) {
        assertThat(userAgreementResponse.getStatementCode()).isEqualTo(statementCode);
        return this;
    }

    public UserAgreementResponseAssert hasStatus(AgreementStatusEnum agreementStatus) {
        assertThat(userAgreementResponse.getAgreementStatus()).isEqualTo(agreementStatus);
        return this;
    }

    public UserAgreementResponseAssert hasVersion(int version) {
        assertThat(userAgreementResponse.getVersion()).isEqualTo(version);
        return this;
    }
}
