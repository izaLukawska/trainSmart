package org.lukawska.trainsmart.config;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.exercisecatalog.domain.repositories.ExerciseRepository;
import org.lukawska.trainsmart.mailing.domain.repositories.MailRepository;
import org.lukawska.trainsmart.statements.domain.repositories.UserAgreementRepository;
import org.lukawska.trainsmart.testutils.builders.*;
import org.lukawska.trainsmart.usermanagement.domain.repository.RefreshTokenRepository;
import org.lukawska.trainsmart.usermanagement.domain.repository.UserRepository;
import org.lukawska.trainsmart.usermanagement.domain.repository.VerificationTokenRepository;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class TestFixtures {

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final VerificationTokenRepository verificationTokenRepository;

    private final ExerciseRepository exerciseRepository;

    private final MailRepository mailRepository;

    private final UserAgreementRepository userAgreementRepository;

    public UserFixtureBuilder user() {
        return new UserFixtureBuilder(userRepository);
    }

    public RefreshTokenFixtureBuilder refreshToken() {
        return new RefreshTokenFixtureBuilder(refreshTokenRepository, this);
    }

    public VerificationTokenFixtureBuilder verificationToken() {
        return new VerificationTokenFixtureBuilder(verificationTokenRepository, this);
    }

    public ExerciseFixtureBuilder exercise() {
        return new ExerciseFixtureBuilder(exerciseRepository);
    }

    public MailingFixtureBuilder mail() {
        return new MailingFixtureBuilder(mailRepository);
    }

    public UserAgreementFixtureBuilder userAgreement() {
        return new UserAgreementFixtureBuilder(userAgreementRepository, this);
    }
}
