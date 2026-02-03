package org.lukawska.trainsmart.usermanagement.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.usermanagement.domain.repository.RefreshTokenRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RefreshTokenCleanupServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenCleanupService refreshTokenCleanupService;

    @Test
    void shouldDeleteAllExpiredTokens() {
        //when
        refreshTokenCleanupService.cleanupExpiredTokens();

        //then
        verify(refreshTokenRepository).deleteAllByExpiresAtBefore(any(Instant.class));
    }
}
