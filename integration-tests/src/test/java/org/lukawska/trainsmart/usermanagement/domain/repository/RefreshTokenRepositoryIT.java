package org.lukawska.trainsmart.usermanagement.domain.repository;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.domain.entity.RefreshToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lukawska.trainsmart.testutils.TestData.username;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({PostgresTestConfig.class, TestFixtures.class})
@Transactional
class RefreshTokenRepositoryIT {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TestFixtures testFixtures;

    @Test
    void shouldDeleteAllRefreshTokenByUsername() {
        //given
        final User user1 = testFixtures.user().save();
        final User user2 = testFixtures.user()
                                       .withUsername(username())
                                       .save();
        final RefreshToken refreshToken = testFixtures.refreshToken()
                                                      .forUser(user1)
                                                      .save();
        final RefreshToken refreshToken1 = testFixtures.refreshToken()
                                                       .forUser(user1)
                                                       .save();
        final RefreshToken refreshToken2 = testFixtures.refreshToken()
                                                       .forUser(user2)
                                                       .save();

        //when
        refreshTokenRepository.deleteAllByUsername(user1.getUsername());

        //then
        assertThat(refreshTokenRepository.findByToken(refreshToken.getToken())).isNotPresent();
        assertThat(refreshTokenRepository.findByToken(refreshToken1.getToken())).isNotPresent();
        assertThat(refreshTokenRepository.findByToken(refreshToken2.getToken())).isPresent();
    }
}
