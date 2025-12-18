package org.lukawska.trainsmart.usermanagement.application.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.mailing.application.dto.MailRequest;
import org.lukawska.trainsmart.mailing.application.service.MailService;
import org.lukawska.trainsmart.mailing.infrastructure.config.MailingProperties;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.domain.entity.ActivationToken;
import org.lukawska.trainsmart.usermanagement.domain.repository.ActivationTokenRepository;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

@Service
@RequiredArgsConstructor
@Slf4j
class ActivationService {

    private final ActivationTokenRepository activationTokenRepository;

    private final MailService mailService;

    private final MailingProperties mailingProperties;

    public void sendActivationMail(User user) {
        String token = UUID.randomUUID().toString();
        ActivationToken activationToken = buildActivationToken(token, user);
        activationTokenRepository.save(activationToken);
        MailRequest mailRequest = buildActivationMailRequest(user.getEmail(), token);

        try {
            mailService.sendMail(mailRequest);
            log.debug("Activation link sent: {}", user.getEmail());
        } catch (MailException e) {
            log.warn("Failed to send activation mail to {}", user.getEmail());
        }
    }

    private ActivationToken buildActivationToken(String token, User user) {
        return new ActivationToken(token, user, Instant.now(), Instant.now().plus(24, ChronoUnit.HOURS));
    }

    private MailRequest buildActivationMailRequest(String email, String token) {
        String activationLink = fromUriString(mailingProperties.getActivationBaseUrl())
                .path("/auth/activate")
                .queryParam("token", token).toUriString();

        String subject = "Account activation";
        String body = """
                Welcome to our sports community.
                To activate your account, please click the link below:
                %s
                """.formatted(activationLink);

        return new MailRequest(List.of(email),
                               Collections.emptyList(),
                               Collections.emptyList(),
                               subject,
                               body,
                               false,
                               Collections.emptyList());
    }
}
