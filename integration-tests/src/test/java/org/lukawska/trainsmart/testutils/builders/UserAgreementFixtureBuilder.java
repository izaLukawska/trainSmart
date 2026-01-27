package org.lukawska.trainsmart.testutils.builders;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.repositories.UserAgreementRepository;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;

import java.util.Optional;

@RequiredArgsConstructor
public class UserAgreementFixtureBuilder {

    private final UserAgreementRepository userAgreementRepository;

    private final TestFixtures testFixtures;

    private User user;

    private String code = "RODO";

    private Integer version = 1;

    private AgreementStatus agreementStatus = AgreementStatus.ACCEPTED;

    public UserAgreementFixtureBuilder withCode(String code) {
        this.code = code;
        return this;
    }

    public UserAgreementFixtureBuilder withUser(User user) {
        this.user = user;
        return this;
    }

    public UserAgreementFixtureBuilder withVersion(Integer version) {
        this.version = version;
        return this;
    }

    public UserAgreement build() {
        User user = Optional.ofNullable(this.user).orElseGet(() -> testFixtures.user().save());
        return new UserAgreement(user, code, version, agreementStatus);
    }

    public UserAgreement save() {
        return userAgreementRepository.save(build());
    }
}
