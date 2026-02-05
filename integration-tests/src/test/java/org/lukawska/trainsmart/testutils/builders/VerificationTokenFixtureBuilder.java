package org.lukawska.trainsmart.testutils.builders;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.domain.entity.VerificationToken;
import org.lukawska.trainsmart.usermanagement.domain.repository.VerificationTokenRepository;
import org.lukawska.trainsmart.usermanagement.domain.valueObject.TokenType;

import java.time.Instant;
import java.util.Optional;

@RequiredArgsConstructor
public class VerificationTokenFixtureBuilder {

    private final VerificationTokenRepository verificationTokenRepository;

    private final TestFixtures testFixtures;

    private String tokenValue = "verificationToken";

    private Instant expiresAt = Instant.now().plusSeconds(200);

    private TokenType tokenType = TokenType.ACTIVATION;

    private User user;

    public VerificationTokenFixtureBuilder forUser(User user) {
        this.user = user;
        return this;
    }

    public VerificationToken build() {
        User user = Optional.ofNullable(this.user).orElseGet(() -> testFixtures.user().save());
        return new VerificationToken(tokenValue, user, expiresAt, tokenType);
    }

    public VerificationToken save() {
        return verificationTokenRepository.save(build());
    }
}
