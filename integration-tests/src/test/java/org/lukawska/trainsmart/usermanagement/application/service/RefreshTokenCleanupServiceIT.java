package org.lukawska.trainsmart.usermanagement.application.service;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.usermanagement.domain.entity.RefreshToken;
import org.lukawska.trainsmart.usermanagement.domain.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import({PostgresTestConfig.class, TestFixtures.class})
@ActiveProfiles("test")
@Transactional
public class RefreshTokenCleanupServiceIT {

    @Autowired
    private RefreshTokenCleanupService refreshTokenCleanupService;

    @Autowired
    private TestFixtures testFixtures;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void shouldDeleteAllExpiredTokenWhenCleanupExpiredTokens() {
        //given
        final Instant expiredDate = Instant.now().minusSeconds(100);
        final RefreshToken expiredToken1 = testFixtures.refreshToken()
                                                       .withExpiresAt(expiredDate)
                                                       .save();
        final RefreshToken expiredToken2 = testFixtures.refreshToken()
                                                       .withExpiresAt(expiredDate)
                                                       .save();
        final RefreshToken validToken = testFixtures.refreshToken()
                                                    .withExpiresAt(Instant.now().plusSeconds(100))
                                                    .save();

        //when
        refreshTokenCleanupService.cleanupExpiredTokens();

        //then
        assertThat(refreshTokenRepository.findByToken(expiredToken1.getToken())).isNotPresent();
        assertThat(refreshTokenRepository.findByToken(expiredToken2.getToken())).isNotPresent();
        assertThat(refreshTokenRepository.findByToken(validToken.getToken())).isPresent();
    }
}
