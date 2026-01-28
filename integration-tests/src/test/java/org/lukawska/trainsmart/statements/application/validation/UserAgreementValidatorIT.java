package org.lukawska.trainsmart.statements.application.validation;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;
import org.lukawska.trainsmart.statements.infra.config.Statement;
import org.lukawska.trainsmart.statements.infra.config.StatementsDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import({PostgresTestConfig.class, TestFixtures.class})
@Transactional
class UserAgreementValidatorIT {

    @Autowired
    private UserAgreementValidator validator;

    @Autowired
    private StatementsDefinition statementsDefinition;

    @Autowired
    private TestFixtures testFixtures;

    @Test
    void shouldReturnStatementFromYamlWhenRequestIsValid() {
        //given
        final UserAgreementRequest request = new UserAgreementRequest("RODO", AgreementStatus.ACCEPTED);

        //when
        Statement statement = validator.validateUserAgreement(request);

        //then
        assertThat(statement.version()).isEqualTo(3);
        assertThat(statement.required()).isTrue();
    }

    @Test
    void shouldReturnTrueWhenUserAgreementIsOutdated() {
        //given
        final UserAgreement oldUserAgreement = testFixtures.userAgreement().save();

        //when
        boolean result = validator.outdatedUserAgreement(oldUserAgreement);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUserAgreementIsUpToDate() {
        //given
        int currentVersion = statementsDefinition.findStatementByCode("RODO")
                                                 .orElseThrow()
                                                 .version();
        final UserAgreement userAgreement = testFixtures.userAgreement()
                                                        .withVersion(currentVersion)
                                                        .build();

        //when
        boolean result = validator.outdatedUserAgreement(userAgreement);

        //then
        assertThat(result).isFalse();
    }
}
