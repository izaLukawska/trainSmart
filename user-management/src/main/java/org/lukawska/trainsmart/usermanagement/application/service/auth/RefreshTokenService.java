package org.lukawska.trainsmart.usermanagement.application.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.application.dto.auth.request.RefreshTokenRequest;
import org.lukawska.trainsmart.usermanagement.application.exception.AuthorizationException;
import org.lukawska.trainsmart.usermanagement.application.exception.ExceptionType;
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
class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final RefreshTokenProperties refreshTokenProperties;

    RefreshToken getRefreshToken(String refreshToken) {
        RefreshToken foundRefreshToken = refreshTokenRepository.findByToken(refreshToken).orElseThrow(
                () -> new AuthorizationException(ExceptionType.INVALID_TOKEN));
        log.debug("Found refresh token {} for user {}", foundRefreshToken.getId(), foundRefreshToken.getUser().getId());

        return foundRefreshToken;
    }

    @Transactional
    RefreshToken createRefreshToken(User user) {
        log.debug("Creating refresh token for user: {}", user.getId());
        Instant expiresAt = Instant.now().plusMillis(refreshTokenProperties.getExpirationMs());
        String token = UUID.randomUUID().toString();

        RefreshToken refreshToken = new RefreshToken(token, user, expiresAt, false);
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);
        log.info("Created and saved new refresh token: {}", savedRefreshToken.getId());

        return savedRefreshToken;
    }

    @Transactional(noRollbackFor = AuthorizationException.class)
    RefreshToken rotateRefreshToken(RefreshTokenRequest refreshTokenRequest) {
        RefreshToken refreshToken = getRefreshToken(refreshTokenRequest.refreshToken());
        log.info("Rotating refresh token: {}", refreshToken.getId());

        if (isInvalid(refreshToken)) {
            log.info("Refresh token {} is invalid.", refreshToken.getId());
            deleteInvalidToken(refreshToken);
            throw new AuthorizationException(ExceptionType.INVALID_TOKEN);
        }

        refreshToken.markAsRevoked();
        log.info("Revoked old token: {}", refreshToken.getId());

        return createRefreshToken(refreshToken.getUser());
    }

    @Transactional
    void deleteInvalidToken(RefreshToken token) {
        if (token.isRevoked()) {
            refreshTokenRepository.deleteAllByUserId(token.getUser().getId());
            log.warn("Security Alert! (revoked token use) Deleted all tokens for user: {}", token.getUser().getId());
        } else {
            refreshTokenRepository.delete(token);
            log.info("Deleted expired token: {}", token.getToken());
        }
    }

    private boolean isInvalid(RefreshToken token) {
        return token.getExpiresAt().isBefore(Instant.now()) || token.isRevoked();
    }
}
