package org.lukawska.trainsmart.usermanagement.application.service.auth;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.commons.jwt.JwtService;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserAccessService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.application.dto.auth.request.LoginRequest;
import org.lukawska.trainsmart.usermanagement.application.dto.auth.request.RefreshTokenRequest;
import org.lukawska.trainsmart.usermanagement.application.dto.auth.response.AuthResponse;
import org.lukawska.trainsmart.usermanagement.domain.entity.RefreshToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    private final UserAccessService userAccessService;

    public AuthResponse login(LoginRequest request) {
        String username = request.username();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, request.password()));
        User user = userAccessService.getUserByUsername(username);

        String accessToken = jwtService.generateAccessToken(user.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        RefreshToken token = refreshTokenService.getValidToken(refreshTokenRequest.refreshToken());
        refreshTokenService.revokeToken(token);

        RefreshToken newToken = refreshTokenService.createRefreshToken(token.getUser());
        String accessToken = jwtService.generateAccessToken(token.getUser().getUsername());

        return new AuthResponse(accessToken, newToken.getToken());
    }
}
