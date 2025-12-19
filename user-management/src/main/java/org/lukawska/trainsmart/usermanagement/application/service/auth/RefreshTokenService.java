package org.lukawska.trainsmart.usermanagement.application.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.application.dto.auth.request.RefreshTokenRequest;
import org.lukawska.trainsmart.usermanagement.application.exception.AuthorizationException;
import org.lukawska.trainsmart.usermanagement.domain.entity.RefreshToken;
import org.lukawska.trainsmart.usermanagement.domain.repository.RefreshTokenRepository;
import org.lukawska.trainsmart.usermanagement.infra.config.RefreshTokenProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final RefreshTokenProperties refreshTokenProperties;

    private final RefreshTokenInvalidationService refreshTokenInvalidationService;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        Instant expiresAt = Instant.now().plusMillis(refreshTokenProperties.getRefreshExpMs());
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken(token, user, expiresAt, false);

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken rotateRefreshToken(RefreshTokenRequest refreshTokenRequest) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenRequest.refreshToken())
                                                          .orElseThrow(AuthorizationException::new);

        if (isInvalid(refreshToken)) {
            refreshTokenInvalidationService.deleteInvalidToken(refreshToken);
            throw new AuthorizationException();
        }

        refreshToken.markAsRevoked();
        return createRefreshToken(refreshToken.getUser());
    }

    private boolean isInvalid(RefreshToken token) {
        return token.getExpiresAt().isBefore(Instant.now()) || token.isRevoked();
    }
}
