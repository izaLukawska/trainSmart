package org.lukawska.trainsmart.usermanagement.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.application.exception.ExceptionType;
import org.lukawska.trainsmart.usermanagement.application.exception.UserManagementException;
import org.lukawska.trainsmart.usermanagement.domain.entity.RefreshToken;
import org.lukawska.trainsmart.usermanagement.domain.repository.RefreshTokenRepository;
import org.lukawska.trainsmart.usermanagement.infra.config.RefreshTokenProperties;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.usermanagement.application.testutil.UserManagementTestData.randomString;
import static org.lukawska.trainsmart.usermanagement.application.testutil.UserManagementTestData.userRefreshToken;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private RefreshTokenProperties refreshTokenProperties;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private static final String USERNAME = randomString();

    private static final String REFRESH_TOKEN_VALUE = randomString();

    private static final User USER = mock(User.class);

    @Test
    void shouldReturnRefreshToken() {
        //given
        final RefreshToken refreshToken = userRefreshToken(USER);
        when(refreshTokenRepository.findByToken(REFRESH_TOKEN_VALUE)).thenReturn(Optional.of(refreshToken));

        //when
        RefreshToken result = refreshTokenService.getRefreshToken(REFRESH_TOKEN_VALUE);

        //then
        assertThat(result.getToken()).isEqualTo(refreshToken.getToken());
        assertThat(result.isRevoked()).isEqualTo(refreshToken.isRevoked());
        assertThat(result.getExpiresAt()).isEqualTo(refreshToken.getExpiresAt());
    }

    @Test
    void shouldReturnCreatedRefreshToken() {
        //given
        final long expirationMs = 10000L;
        when(refreshTokenProperties.getExpirationMs()).thenReturn(expirationMs);

        //when
        RefreshToken result = refreshTokenService.createRefreshToken(USER);

        //then
        assertThat(result.getToken()).isNotNull();
        assertThat(result.isRevoked()).isFalse();
        assertThat(result.getExpiresAt()).isAfter(Instant.now().minusMillis(expirationMs));
        assertThat(result.getExpiresAt()).isBefore(Instant.now().plusMillis(expirationMs));
    }

    @Test
    void shouldRotateRefreshTokenWhenTokenValid() {
        //given
        final RefreshToken oldRefreshToken = userRefreshToken(USER);
        when(refreshTokenRepository.findByToken(REFRESH_TOKEN_VALUE)).thenReturn(
                Optional.of(oldRefreshToken));

        //when
        RefreshToken result = refreshTokenService.rotateRefreshToken(REFRESH_TOKEN_VALUE);

        //then
        assertThat(oldRefreshToken.isRevoked()).isTrue();
        assertThat(result.getToken()).isNotNull();
        assertThat(result.isRevoked()).isFalse();
    }

    @Test
    void shouldDeleteAllUserRefreshTokensAndThrowInvalidTokenExceptionWhenRotateRefreshToken() {
        //given
        final RefreshToken refreshToken = new RefreshToken(REFRESH_TOKEN_VALUE, USER, Instant.MIN, true);
        when(refreshTokenRepository.findByToken(REFRESH_TOKEN_VALUE)).thenReturn(Optional.of(refreshToken));

        //when && then
        assertThatThrownBy(() -> refreshTokenService.rotateRefreshToken(REFRESH_TOKEN_VALUE))
                .isInstanceOf(UserManagementException.class)
                .hasMessage(ExceptionType.INVALID_TOKEN.getMessage());
    }

    @Test
    void shouldDeleteAllRefreshTokenForUserWhenTokenRevoked() {
        //given
        final RefreshToken refreshToken = new RefreshToken(REFRESH_TOKEN_VALUE, USER, Instant.MAX, true);
        when(refreshToken.getUser().getUsername()).thenReturn(USERNAME);

        //when
        refreshTokenService.deleteInvalidRefreshToken(refreshToken);

        //then
        verify(refreshTokenRepository).deleteAllByUsername(USERNAME);
    }

    @Test
    void shouldDeleteAllRefreshTokenForUserWhenTokenNotRevoked() {
        //given
        final RefreshToken refreshToken = userRefreshToken(USER);

        //when
        refreshTokenService.deleteInvalidRefreshToken(refreshToken);

        //then
        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void shouldDeleteAllRefreshTokenForUser() {
        //when
        refreshTokenService.deleteRefreshTokenForUser(USERNAME);

        //then
        verify(refreshTokenRepository).deleteAllByUsername(USERNAME);
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenGetRefreshToken() {
        //given
        when(refreshTokenRepository.findByToken(REFRESH_TOKEN_VALUE)).thenReturn(Optional.empty());

        //when && then
        assertThatThrownBy(() -> refreshTokenService.getRefreshToken(REFRESH_TOKEN_VALUE))
                .isInstanceOf(UserManagementException.class)
                .hasMessage(ExceptionType.INVALID_TOKEN.getMessage());
    }
}
