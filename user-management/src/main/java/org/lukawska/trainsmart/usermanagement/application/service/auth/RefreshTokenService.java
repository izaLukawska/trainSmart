package org.lukawska.trainsmart.usermanagement.application.service.auth;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.application.exception.AuthorizationException;
import org.lukawska.trainsmart.usermanagement.domain.entity.RefreshToken;
import org.lukawska.trainsmart.usermanagement.domain.repository.RefreshTokenRepository;
import org.lukawska.trainsmart.usermanagement.infra.config.RefreshTokenProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final RefreshTokenProperties refreshTokenProperties;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        Instant expiresAt = Instant.now().plusMillis(refreshTokenProperties.getRefreshExpMs());
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken(token, user, expiresAt, false);

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void revokeToken(RefreshToken token) {
        token.markAsRevoked();
        refreshTokenRepository.save(token);
    }

    @Transactional
    public void deleteToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    @Transactional
    public void deleteAllTokensByUser(User user) {
        refreshTokenRepository.deleteAllByUser(user);
    }

    @Transactional
    public RefreshToken validateAndGetToken(String tokenValue) {
        Instant currentTime = Instant.now();
        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue)
                                                          .orElseThrow(AuthorizationException::new);

        if (refreshToken.isRevoked()) {
            User user = refreshToken.getUser();
            refreshTokenRepository.deleteAllByUser(user);
            throw new AuthorizationException();
        }

        if (refreshToken.getExpiresAt().isBefore(currentTime)) {
            refreshTokenRepository.deleteByToken(refreshToken.getToken());
            throw new AuthorizationException();
        }

        return refreshToken;
    }
}
