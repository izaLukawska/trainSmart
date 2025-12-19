package org.lukawska.trainsmart.usermanagement.application.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.usermanagement.domain.entity.RefreshToken;
import org.lukawska.trainsmart.usermanagement.domain.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
class RefreshTokenInvalidationService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteInvalidToken(RefreshToken token) {
        if (token.isRevoked()) {
            log.warn("Security Alert! Revoked token used for user: {}", token.getUser().getId());
            refreshTokenRepository.deleteAllByUserId(token.getUser().getId());
        } else {
            log.info("Deleting expired token: {}", token.getToken());
            refreshTokenRepository.delete(token);
        }
    }
}
