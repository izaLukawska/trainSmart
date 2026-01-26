package org.lukawska.trainsmart.usermanagement.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.commons.jwt.JwtService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.domain.entity.RefreshToken;
import org.lukawska.trainsmart.usermanagement.model.AuthResponse;
import org.lukawska.trainsmart.usermanagement.model.LoginRequest;
import org.lukawska.trainsmart.usermanagement.model.LogoutRequest;
import org.lukawska.trainsmart.usermanagement.model.RefreshTokenRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lukawska.trainsmart.usermanagement.application.testutil.UserManagementTestData.user;
import static org.lukawska.trainsmart.usermanagement.application.testutil.UserManagementTestData.userRefreshToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private static final String accessTokenValue = UUID.randomUUID().toString();

    @Test
    void shouldLoginUser() {
        //given
        final User user = user();
        final LoginRequest loginRequest = new LoginRequest(user.getUsername(), user.getPassword());
        final RefreshToken refreshToken = userRefreshToken(user);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userService.getUserByUsername(user.getUsername())).thenReturn(user);
        when(jwtService.generateAccessToken(user.getUsername())).thenReturn(accessTokenValue);
        when(refreshTokenService.createRefreshToken(user)).thenReturn(refreshToken);

        //when
        AuthResponse result = authenticationService.login(loginRequest);

        //then
        assertThat(result.getAccessToken()).isEqualTo(accessTokenValue);
        assertThat(result.getRefreshToken()).isEqualTo(refreshToken.getToken());
    }

    @Test
    void shouldReturnRefreshedToken() {
        //given
        final User user = user();
        final RefreshToken refreshToken = userRefreshToken(user);
        final RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(refreshToken.getToken());

        when(refreshTokenService.rotateRefreshToken(refreshTokenRequest.getRefreshToken())).thenReturn(refreshToken);
        when(jwtService.generateAccessToken(user.getUsername())).thenReturn(accessTokenValue);

        //when
        AuthResponse result = authenticationService.refreshToken(refreshTokenRequest);

        //then
        assertThat(result.getAccessToken()).isEqualTo(accessTokenValue);
        assertThat(result.getRefreshToken()).isEqualTo(refreshToken.getToken());
    }

    @Test
    void shouldLogoutUser() {
        //given
        final RefreshToken refreshToken = userRefreshToken(mock(User.class));
        final LogoutRequest logoutRequest = new LogoutRequest(refreshToken.getToken());
        when(refreshTokenService.getRefreshToken(refreshToken.getToken())).thenReturn(refreshToken);

        //when
        authenticationService.logout(logoutRequest);

        //then
        assertThat(refreshToken.isRevoked()).isTrue();
    }
}
