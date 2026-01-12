package org.lukawska.trainsmart.usermanagement.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.commons.jwt.JwtService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.application.exception.UserManagementException;
import org.lukawska.trainsmart.usermanagement.domain.entity.RefreshToken;
import org.lukawska.trainsmart.usermanagement.model.AuthResponse;
import org.lukawska.trainsmart.usermanagement.model.LoginRequest;
import org.lukawska.trainsmart.usermanagement.model.LogoutRequest;
import org.lukawska.trainsmart.usermanagement.model.RefreshTokenRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    private final UserService userService;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String username = request.getUsername();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, request.getPassword()));
        User user = userService.getUserByUsername(username);

        log.info("Generating access token and refresh token for user {}", user.getId());
        String accessToken = jwtService.generateAccessToken(user.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        log.info("Login success. Created refresh token {}.", refreshToken.getId());

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    @Transactional(noRollbackFor = UserManagementException.class)
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(refreshTokenRequest.getRefreshToken());
        String accessToken = jwtService.generateAccessToken(newRefreshToken.getUser().getUsername());

        return new AuthResponse(accessToken, newRefreshToken.getToken());
    }

    @Transactional
    public void logout(LogoutRequest logoutRequest) {
        RefreshToken refreshToken = refreshTokenService.getRefreshToken(logoutRequest.getRefreshToken());
        refreshToken.markAsRevoked();
        log.info("Revoked refreshToken {}", refreshToken.getId());
        SecurityContextHolder.clearContext();
    }
}
