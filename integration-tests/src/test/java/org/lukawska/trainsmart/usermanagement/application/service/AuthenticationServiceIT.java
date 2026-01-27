package org.lukawska.trainsmart.usermanagement.application.service;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.commons.jwt.JwtService;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.testutils.TestData;
import org.lukawska.trainsmart.usermanagement.domain.entity.RefreshToken;
import org.lukawska.trainsmart.usermanagement.domain.repository.RefreshTokenRepository;
import org.lukawska.trainsmart.usermanagement.model.AuthResponse;
import org.lukawska.trainsmart.usermanagement.model.LoginRequest;
import org.lukawska.trainsmart.usermanagement.model.LogoutRequest;
import org.lukawska.trainsmart.usermanagement.model.RefreshTokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.trilead.ssh2.auth.AuthenticationManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import({PostgresTestConfig.class, TestFixtures.class})
@ActiveProfiles("test")
@Transactional
public class AuthenticationServiceIT {

    @Autowired
    private TestFixtures testFixtures;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldReturnAuthResponseWhenLogin() {
        //given
        final String rawPassword = TestData.rawPassword();
        final User user = testFixtures.user()
                                      .withPassword(passwordEncoder.encode(rawPassword))
                                      .save();
        final LoginRequest loginRequest = new LoginRequest(user.getUsername(), rawPassword);

        //when
        AuthResponse result = authenticationService.login(loginRequest);

        //then
        assertThat(result.getAccessToken()).isNotBlank();
        assertThat(result.getRefreshToken()).isNotBlank();
        assertThat(jwtService.validToken(result.getAccessToken())).isTrue();

        Optional<RefreshToken> assignedRefreshToken = refreshTokenRepository.findByToken(result.getRefreshToken());
        assertThat(assignedRefreshToken.isPresent()).isTrue();
        assertThat(result.getRefreshToken()).isEqualTo(assignedRefreshToken.get().getToken());
    }

    @Test
    void shouldThrowDisabledExceptionWhenUserAccountInactive() {
        //given
        final User user = testFixtures.user()
                                      .inactive()
                                      .save();
        final LoginRequest loginRequest = new LoginRequest(user.getUsername(), user.getPassword());

        //when && then
        assertThatThrownBy(() -> authenticationService.login(loginRequest)).isInstanceOf(DisabledException.class);
    }

    @Test
    void shouldThrowBadCredentialsWhenUserInvalidPassword() {
        //given
        final User user = testFixtures.user()
                                      .save();
        final LoginRequest loginRequest = new LoginRequest(user.getUsername(), TestData.rawPassword());

        //when && then
        assertThatThrownBy(() -> authenticationService.login(loginRequest)).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void shouldReturnAuthResponseWithNewTokensWhenRefreshToken() {
        //given
        final RefreshToken refreshToken = testFixtures.refreshToken()
                                                      .save();
        final RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(refreshToken.getToken());

        //when
        AuthResponse result = authenticationService.refreshToken(refreshTokenRequest);

        //then
        RefreshToken newRefreshToken = refreshTokenRepository.findAll().getFirst();
        assertThat(result.getRefreshToken()).isEqualTo(newRefreshToken.getToken());
        assertThat(result.getRefreshToken()).isNotEqualTo(refreshTokenRequest.getRefreshToken());
        assertThat(newRefreshToken.getUser().getId()).isEqualTo(refreshToken.getUser().getId());
        assertThat(result.getAccessToken()).isNotBlank();
    }

    @Test
    void shouldRevokeRefreshTokenWhenLogout() {
        //given
        final RefreshToken refreshToken = testFixtures.refreshToken()
                                                      .save();
        final LogoutRequest logoutRequest = new LogoutRequest(refreshToken.getToken());

        //when
        authenticationService.logout(logoutRequest);

        //then
        assertThat(refreshToken.isRevoked()).isTrue();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
