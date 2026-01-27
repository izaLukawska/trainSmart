package org.lukawska.trainsmart.statements.application.service;

import org.junit.jupiter.api.BeforeEach;
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

    private User user;

    @BeforeEach
    void setUp() {
        user = testFixtures.user().save();
    }

    @Test
    void shouldCreateAndSaveAgreementWhenSignAgreement() {
        //given
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
        final UserAgreement oldUserAgreement = outdatedUserAgreement(user, "GPDR");
        userAgreementRepository.saveAndFlush(oldUserAgreement);

        //when
        UserAgreementResponse result = userAgreementService.signAgreement(user.getId(), request);

        //then
        assertThat(result.agreementStatus()).isEqualTo(oldUserAgreement.getStatus());
        assertThat(result.statementCode()).isEqualTo(oldUserAgreement.getStatementCode());
        assertThat(result.version()).isEqualTo(5);
    }

    @Test
    void shouldReturnRequiredStatementToSignByUserId() {
        //given
        final Long userId = user.getId();
        final UserAgreement oldUserAgreement1 = outdatedUserAgreement(user, "GPDR");
        final UserAgreement oldUserAgreement2 = outdatedUserAgreement(user, "RODO");
        userAgreementRepository.saveAllAndFlush(List.of(oldUserAgreement1, oldUserAgreement2));

        //when
        List<UserAgreementResponse> result = userAgreementService.getRequiredStatementsToSign(userId);

        //then
        UserAgreementResponse userAgreement1 = result.getFirst();
        assertThat(userAgreement1.agreementStatus()).isEqualTo(oldUserAgreement1.getStatus());
        assertThat(userAgreement1.statementCode()).isEqualTo(oldUserAgreement1.getStatementCode());
        assertThat(userAgreement1.version()).isEqualTo(oldUserAgreement1.getStatementVersion());

        UserAgreementResponse userAgreement2 = result.getLast();
        assertThat(userAgreement2.agreementStatus()).isEqualTo(oldUserAgreement2.getStatus());
        assertThat(userAgreement2.statementCode()).isEqualTo(oldUserAgreement2.getStatementCode());
        assertThat(userAgreement2.version()).isEqualTo(oldUserAgreement2.getStatementVersion());
    }

    private UserAgreementRequest userAgreementRequest() {
        return new UserAgreementRequest("RODO", AgreementStatus.ACCEPTED);
    }

    private UserAgreement outdatedUserAgreement(User user, String code) {
        return new UserAgreement(user, code, 1, AgreementStatus.ACCEPTED);
    }
}
