package org.lukawska.trainsmart.usermanagement.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.application.dto.request.auth.RefreshTokenRequest;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    void shouldReturnRefreshToken() {
        //given
        final String refreshTokenValue = UUID.randomUUID().toString();
        final RefreshToken refreshToken = userRefreshToken(mock(User.class));
        when(refreshTokenRepository.findByToken(refreshTokenValue)).thenReturn(Optional.of(refreshToken));

        //when
        RefreshToken result = refreshTokenService.getRefreshToken(refreshTokenValue);

        //then
        assertThat(result.getToken()).isEqualTo(refreshToken.getToken());
        assertThat(result.isRevoked()).isEqualTo(refreshToken.isRevoked());
        assertThat(result.getExpiresAt()).isEqualTo(refreshToken.getExpiresAt());
    }

    @Test
    void shouldReturnCreatedRefreshToken() {
        //given
        final User user = mock(User.class);
        final long expirationMs = 10000L;
        when(refreshTokenProperties.getExpirationMs()).thenReturn(expirationMs);

        //when
        RefreshToken result = refreshTokenService.createRefreshToken(user);

        //then
        assertThat(result.getToken()).isNotNull();
        assertThat(result.isRevoked()).isFalse();
        assertThat(result.getExpiresAt()).isAfter(Instant.now().minusMillis(expirationMs));
        assertThat(result.getExpiresAt()).isBefore(Instant.now().plusMillis(expirationMs));
    }

    @Test
    void shouldRotateRefreshTokenWhenTokenValid() {
        //given
        final String tokenValue = UUID.randomUUID().toString();
        final RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(tokenValue);
        final RefreshToken oldRefreshToken = userRefreshToken(mock(User.class));
        when(refreshTokenRepository.findByToken(refreshTokenRequest.refreshToken())).thenReturn(
                Optional.of(oldRefreshToken));

        //when
        RefreshToken result = refreshTokenService.rotateRefreshToken(refreshTokenRequest);

        //then
        assertThat(oldRefreshToken.isRevoked()).isTrue();
        assertThat(result.getToken()).isNotNull();
        assertThat(result.isRevoked()).isFalse();
    }

    @Test
    void shouldDeleteAllUserRefreshTokensAndThrowInvalidTokenExceptionWhenRotateRefreshToken() {
        //given
        final String tokenValue = UUID.randomUUID().toString();
        final RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(tokenValue);
        final RefreshToken refreshToken = new RefreshToken(tokenValue, mock(User.class), Instant.MIN, true);
        when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(refreshToken));

        //when && then
        assertThatThrownBy(() -> refreshTokenService.rotateRefreshToken(refreshTokenRequest))
                .isInstanceOf(UserManagementException.class)
                .hasMessage(ExceptionType.INVALID_TOKEN.getMessage());
    }

    @Test
    void shouldDeleteAllRefreshTokenForUserWhenTokenRevoked() {
        //given
        final String tokenValue = UUID.randomUUID().toString();
        final RefreshToken refreshToken = new RefreshToken(tokenValue, mock(User.class), Instant.MAX, true);
        final String username = "username";
        when(refreshToken.getUser().getUsername()).thenReturn(username);

        //when
        refreshTokenService.deleteInvalidRefreshToken(refreshToken);

        //then
        verify(refreshTokenRepository).deleteAllByUsername(username);
    }

    @Test
    void shouldDeleteAllRefreshTokenForUserWhenTokenNotRevoked() {
        //given
        final RefreshToken refreshToken = userRefreshToken(mock(User.class));

        //when
        refreshTokenService.deleteInvalidRefreshToken(refreshToken);

        //then
        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void shouldDeleteAllRefreshTokenForUser() {
        //given
        final String username = "username";

        //when
        refreshTokenService.deleteRefreshTokenForUser(username);

        //then
        verify(refreshTokenRepository).deleteAllByUsername(username);
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenGetRefreshToken() {
        //given
        final String refreshTokenValue = UUID.randomUUID().toString();
        when(refreshTokenRepository.findByToken(refreshTokenValue)).thenReturn(Optional.empty());

        //when && then
        assertThatThrownBy(() -> refreshTokenService.getRefreshToken(refreshTokenValue))
                .isInstanceOf(UserManagementException.class)
                .hasMessage(ExceptionType.INVALID_TOKEN.getMessage());
    }
}
