package org.lukawska.trainsmart.statements.application.service;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementResponse;
import org.lukawska.trainsmart.statements.application.services.UserAgreementService;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.repositories.UserAgreementRepository;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Import({PostgresTestConfig.class, TestFixtures.class})
@Transactional
class UserAgreementServiceIT {

    @Autowired
    private UserAgreementRepository userAgreementRepository;

    @Autowired
    private UserAgreementService userAgreementService;

    @Autowired
    private TestFixtures testFixtures;

    @Test
    void shouldCreateAndSaveAgreementWhenSignAgreement() {
        //given
        final User user = testFixtures.user()
                                      .save();
        final UserAgreementRequest request = userAgreementRequest();

        //when
        UserAgreementResponse result = userAgreementService.signAgreement(user.getId(), request);

        //then
        assertThat(result.statementCode()).isEqualTo(request.statementCode());
        assertThat(result.agreementStatus()).isEqualTo(request.agreementStatus());
    }

    @Test
    void shouldUpdateAndSaveAgreementWhenSignAgreement() {
        //given
        final UserAgreementRequest request = userAgreementRequest();
        final UserAgreement oldUserAgreement = testFixtures.userAgreement()
                                                           .withCode(request.statementCode())
                                                           .save();

        //when
        UserAgreementResponse result = userAgreementService.signAgreement(oldUserAgreement.getUser().getId(), request);

        //then
        assertThat(result.agreementStatus()).isEqualTo(oldUserAgreement.getStatus());
        assertThat(result.statementCode()).isEqualTo(oldUserAgreement.getStatementCode());
        assertThat(result.version()).isEqualTo(3);
    }

    @Test
    void shouldReturnRequiredStatementToSignByUserId() {
        //given
        final User user = testFixtures.user()
                                      .save();
        final UserAgreement oldUserAgreement1 = testFixtures.userAgreement()
                                                            .withUser(user)
                                                            .save();
        final UserAgreement oldUserAgreement2 = testFixtures.userAgreement()
                                                            .withUser(user)
                                                            .withCode("PESEL")
                                                            .save();
        userAgreementRepository.saveAllAndFlush(List.of(oldUserAgreement1, oldUserAgreement2));

        //when
        List<UserAgreementResponse> result = userAgreementService.getRequiredStatementsToSign(user.getId());

        //then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserAgreementResponse::statementCode)
                          .containsExactlyInAnyOrder(oldUserAgreement1.getStatementCode(),
                                                     oldUserAgreement2.getStatementCode());
    }

    private UserAgreementRequest userAgreementRequest() {
        return new UserAgreementRequest("RODO", AgreementStatus.ACCEPTED);
    }
}
