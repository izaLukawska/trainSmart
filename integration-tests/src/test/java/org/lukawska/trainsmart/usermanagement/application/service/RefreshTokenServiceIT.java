package org.lukawska.trainsmart.usermanagement.application.service;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.application.exception.ExceptionType;
import org.lukawska.trainsmart.usermanagement.application.exception.UserManagementException;
import org.lukawska.trainsmart.usermanagement.domain.entity.RefreshToken;
import org.lukawska.trainsmart.usermanagement.domain.repository.RefreshTokenRepository;
import org.lukawska.trainsmart.usermanagement.infra.config.RefreshTokenProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Import({PostgresTestConfig.class, TestFixtures.class})
@ActiveProfiles("test")
@Transactional
class RefreshTokenServiceIT {

    @Autowired
    private RefreshTokenProperties refreshTokenProperties;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private TestFixtures testFixtures;

    @Test
    void shouldReturnRefreshTokenByTokenValue() {
        //given
        final RefreshToken refreshToken = testFixtures.refreshToken().save();

        //when
        RefreshToken result = refreshTokenService.getRefreshToken(refreshToken.getToken());

        //then
        assertThat(result.getId()).isEqualTo(refreshToken.getId());
        assertThat(result.getToken()).isEqualTo(refreshToken.getToken());
        assertThat(result.isRevoked()).isEqualTo(refreshToken.isRevoked());
        assertThat(result.getExpiresAt()).isEqualTo(refreshToken.getExpiresAt());
        assertThat(result.getUser().getId()).isEqualTo(refreshToken.getUser().getId());
    }

    @Test
    void shouldReturnRefreshTokenWhenCreateRefreshToken() {
        //given
        final RefreshToken refreshToken = testFixtures.refreshToken().build();
        final Instant expectedExpiresAt = Instant.now().plusMillis(refreshTokenProperties.getExpirationMs());

        //when
        RefreshToken result = refreshTokenService.createRefreshToken(refreshToken.getUser());

        //then
        assertThat(result.getId()).isPositive();
        assertThat(result.getToken()).isNotNull();
        assertThat(result.isRevoked()).isEqualTo(refreshToken.isRevoked());
        assertThat(result.getExpiresAt()).isCloseTo(expectedExpiresAt, within(1, ChronoUnit.SECONDS));
        assertThat(result.getUser().getId()).isEqualTo(refreshToken.getUser().getId());
    }

    @Test
    void shouldReturnRefreshTokenWhenRotateRefreshTokenByTokenValue() {
        //given
        final RefreshToken refreshToken = testFixtures.refreshToken().save();
        final Instant expectedExpiresAt = Instant.now().plusMillis(refreshTokenProperties.getExpirationMs());

        //when
        RefreshToken result = refreshTokenService.rotateRefreshToken(refreshToken.getToken());

        //then
        assertThat(result.getId()).isPositive();
        assertThat(result.getToken()).isNotNull();
        assertThat(result.isRevoked()).isFalse();
        assertThat(result.getExpiresAt()).isCloseTo(expectedExpiresAt, within(1, ChronoUnit.SECONDS));
        assertThat(result.getUser().getId()).isEqualTo(refreshToken.getUser().getId());
        assertThat(refreshToken.isRevoked()).isTrue();
    }

    @Test
    void shouldThrowUserManagementExceptionAndDeleteTokenWhenRotateInvalidRefreshTokenNotRevoked() {
        //given
        final RefreshToken refreshToken = testFixtures.refreshToken()
                                                      .withExpiresAt(Instant.now().minusSeconds(100))
                                                      .save();
        final String tokenValue = refreshToken.getToken();

        //when && then
        assertThatThrownBy(() -> refreshTokenService.rotateRefreshToken(tokenValue))
                .isInstanceOf(UserManagementException.class)
                .hasMessage(ExceptionType.INVALID_TOKEN.getMessage());
        assertThat(refreshTokenRepository.findByToken(tokenValue)).isNotPresent();
    }

    @Test
    void shouldThrowUserManagementExceptionAndDeleteAllRefreshTokensWhenRotateInvalidRefreshTokenRevoked() {
        //given
        final User user = testFixtures.user().save();
        final RefreshToken revokedToken = testFixtures.refreshToken()
                                                      .forUser(user)
                                                      .isRevoked(true)
                                                      .save();
        final RefreshToken validToken = testFixtures.refreshToken()
                                                    .forUser(user)
                                                    .save();
        final String revokedTokenValue = revokedToken.getToken();

        //when && then
        assertThatThrownBy(() -> refreshTokenService.rotateRefreshToken(revokedTokenValue))
                .isInstanceOf(UserManagementException.class)
                .hasMessage(ExceptionType.INVALID_TOKEN.getMessage());

        assertThat(refreshTokenRepository.findByToken(revokedTokenValue)).isNotPresent();
        assertThat(refreshTokenRepository.findByToken(validToken.getToken())).isNotPresent();
    }
}
