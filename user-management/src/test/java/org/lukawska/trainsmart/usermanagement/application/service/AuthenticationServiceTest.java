package org.lukawska.trainsmart.usermanagement.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.commons.jwt.JwtService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.application.dto.request.auth.LoginRequest;
import org.lukawska.trainsmart.usermanagement.application.dto.request.auth.LogoutRequest;
import org.lukawska.trainsmart.usermanagement.application.dto.request.auth.RefreshTokenRequest;
import org.lukawska.trainsmart.usermanagement.application.dto.response.AuthResponse;
import org.lukawska.trainsmart.usermanagement.domain.entity.RefreshToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

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

    @Test
    void shouldLoginUser() {
        //given
        final User user = user();
        final LoginRequest loginRequest = new LoginRequest(user.getUsername(), user.getPassword());
        final String accessToken = "accessToken";
        final RefreshToken refreshToken = userRefreshToken(user);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userService.getUserByUsername(user.getUsername())).thenReturn(user);
        when(jwtService.generateAccessToken(user.getUsername())).thenReturn(accessToken);
        when(refreshTokenService.createRefreshToken(user)).thenReturn(refreshToken);

        //when
        AuthResponse result = authenticationService.login(loginRequest);

        //then
        assertThat(result.accessToken()).isEqualTo(accessToken);
        assertThat(result.refreshToken()).isEqualTo(refreshToken.getToken());
    }

    @Test
    void shouldReturnRefreshedToken() {
        //given
        final User user = user();
        final RefreshToken refreshToken = userRefreshToken(user);
        final RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(refreshToken.getToken());
        final String accessToken = "accessToken";

        when(refreshTokenService.rotateRefreshToken(refreshTokenRequest)).thenReturn(refreshToken);
        when(jwtService.generateAccessToken(user.getUsername())).thenReturn(accessToken);

        //when
        AuthResponse result = authenticationService.refreshToken(refreshTokenRequest);

        //then
        assertThat(result.accessToken()).isEqualTo(accessToken);
        assertThat(result.refreshToken()).isEqualTo(refreshToken.getToken());
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
