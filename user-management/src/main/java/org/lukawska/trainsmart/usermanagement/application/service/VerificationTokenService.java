package org.lukawska.trainsmart.usermanagement.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.mailing.application.dto.MailRequest;
import org.lukawska.trainsmart.mailing.application.service.MailService;
import org.lukawska.trainsmart.mailing.infrastructure.config.MailingProperties;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.application.exception.ExceptionType;
import org.lukawska.trainsmart.usermanagement.application.exception.UserException;
import org.lukawska.trainsmart.usermanagement.application.resolvers.MailContentProviderResolver;
import org.lukawska.trainsmart.usermanagement.application.strategy.MailContentProvider;
import org.lukawska.trainsmart.usermanagement.domain.entity.VerificationToken;
import org.lukawska.trainsmart.usermanagement.domain.repository.VerificationTokenRepository;
import org.lukawska.trainsmart.usermanagement.domain.valueObject.TokenType;
import org.lukawska.trainsmart.usermanagement.infra.config.VerificationTokenProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
class VerificationTokenService {

    private final MailContentProviderResolver mailContentProviderResolver;

    private final VerificationTokenRepository verificationTokenRepository;

    private final MailService mailService;

    private final MailingProperties mailingProperties;

    private final VerificationTokenProperties verificationTokenProperties;

    @Transactional
    void sendVerificationMail(User user, TokenType tokenType) {
        verificationTokenRepository.deleteByUserAndTokenType(user, tokenType);

        log.info("Sending {} mail for user {}", tokenType.name(), user.getId());

        String token = UUID.randomUUID().toString();
        Instant expirationDate = Instant.now().plus(verificationTokenProperties.getDurationHours());
        VerificationToken verificationToken = new VerificationToken(token, user, expirationDate, tokenType);
        verificationTokenRepository.save(verificationToken);

        log.info("Created verification token {}", verificationToken.getId());

        MailContentProvider provider = mailContentProviderResolver.getProvider(tokenType);
        MailRequest mailRequest = provider.createMailRequest(user.getEmail(), mailingProperties.getBaseUrl(), token);

        mailService.sendMail(mailRequest);
    }

    @Transactional
    VerificationToken consumeActiveVerificationToken(String token) {
        log.debug("Retrieving valid verification token {}", token);
        VerificationToken verificationToken = getValidActivationToken(token);

        verificationTokenRepository.delete(verificationToken);
        log.info("Used verification token: {}", verificationToken.getId());

        return verificationToken;
    }

    private VerificationToken getValidActivationToken(String token) {
        return verificationTokenRepository.findByToken(token)
                                          .filter(vt -> !vt.isExpired())
                                          .orElseThrow(
                                                  () -> new UserException(ExceptionType.VERIFICATION_TOKEN_INVALID));
    }
}
