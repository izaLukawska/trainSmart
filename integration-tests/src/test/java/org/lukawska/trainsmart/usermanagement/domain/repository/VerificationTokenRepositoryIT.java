package org.lukawska.trainsmart.usermanagement.domain.repository;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.domain.entity.VerificationToken;
import org.lukawska.trainsmart.usermanagement.domain.valueObject.TokenType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({PostgresTestConfig.class, TestFixtures.class})
@ActiveProfiles("test")
@Transactional
class VerificationTokenRepositoryIT {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private TestFixtures testFixtures;

    @Test
    void shouldDeleteTokenByUserAndTokenType() {
        //given
        final VerificationToken verificationToken = testFixtures.verificationToken()
                                                                .save();
        final TokenType tokenType = verificationToken.getTokenType();
        final User user = verificationToken.getUser();

        //when
        verificationTokenRepository.deleteByUserAndTokenType(user, tokenType);

        //then
        assertThat(verificationTokenRepository.findById(verificationToken.getId())).isEmpty();
    }
}
