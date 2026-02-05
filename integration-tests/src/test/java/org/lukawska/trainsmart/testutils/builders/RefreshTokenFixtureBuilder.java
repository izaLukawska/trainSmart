package org.lukawska.trainsmart.testutils.builders;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.domain.entity.RefreshToken;
import org.lukawska.trainsmart.usermanagement.domain.repository.RefreshTokenRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class RefreshTokenFixtureBuilder {

    private final RefreshTokenRepository refreshTokenRepository;

    private final TestFixtures testFixtures;

    private String tokenValue = UUID.randomUUID().toString();

    private User user;

    private Instant expiresAt = Instant.now().plusSeconds(200);

    private boolean isRevoked = false;

    public RefreshTokenFixtureBuilder forUser(User user) {
        this.user = user;
        return this;
    }

    public RefreshTokenFixtureBuilder withExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }

    public RefreshTokenFixtureBuilder isRevoked(boolean isRevoked) {
        this.isRevoked = isRevoked;
        return this;
    }

    public RefreshToken build() {
        User user = Optional.ofNullable(this.user).orElseGet(() -> testFixtures.user().save());
        return new RefreshToken(tokenValue, user, expiresAt, isRevoked);
    }

    public RefreshToken save() {
        return refreshTokenRepository.save(build());
    }
}
