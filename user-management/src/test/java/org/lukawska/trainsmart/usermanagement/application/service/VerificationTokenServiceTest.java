package org.lukawska.trainsmart.usermanagement.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.mailing.application.dto.MailRequest;
import org.lukawska.trainsmart.mailing.application.service.MailService;
import org.lukawska.trainsmart.mailing.infrastructure.config.MailingProperties;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.application.exception.ExceptionType;
import org.lukawska.trainsmart.usermanagement.application.exception.UserManagementException;
import org.lukawska.trainsmart.usermanagement.application.resolvers.MailContentProviderResolver;
import org.lukawska.trainsmart.usermanagement.application.strategy.MailContentProvider;
import org.lukawska.trainsmart.usermanagement.domain.entity.VerificationToken;
import org.lukawska.trainsmart.usermanagement.domain.repository.VerificationTokenRepository;
import org.lukawska.trainsmart.usermanagement.domain.valueObject.TokenType;
import org.lukawska.trainsmart.usermanagement.infra.config.VerificationTokenProperties;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.usermanagement.application.testutil.UserManagementTestData.user;
import static org.lukawska.trainsmart.usermanagement.application.testutil.UserManagementTestData.verificationToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationTokenServiceTest {

    @Mock
    private MailContentProviderResolver mailContentProviderResolver;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private MailService mailService;

    @Mock
    private MailingProperties mailingProperties;

    @Mock
    private VerificationTokenProperties verificationTokenProperties;

    @InjectMocks
    private VerificationTokenService verificationTokenService;

    @Test
    void shouldSendVerificationMail() {
        //given
        final User user = user();
        final TokenType tokenType = TokenType.ACTIVATION;
        final VerificationToken verificationToken = verificationToken(user);
        final MailContentProvider mailContentProvider = mock(MailContentProvider.class);
        when(verificationTokenProperties.getExpirationHours()).thenReturn(24L);
        when(mailingProperties.getBaseUrl()).thenReturn("https://example.com");
        when(verificationTokenRepository.save(any())).thenReturn(verificationToken);
        when(mailContentProviderResolver.getProvider(tokenType)).thenReturn(mailContentProvider);
        when(mailContentProvider.createMailRequest(anyString(), anyString(), anyString()))
                .thenReturn(mock(MailRequest.class));

        //when
        verificationTokenService.sendVerificationMail(user, tokenType);

        //then
        verify(verificationTokenRepository, times(1)).deleteByUserAndTokenType(user, tokenType);
        verify(mailService).sendMail(any());
    }

    @Test
    void shouldConsumeActiveVerificationToken() {
        //given
        final User user = user();
        final VerificationToken verificationToken = verificationToken(user);
        final String token = verificationToken.getToken();
        when(verificationTokenRepository.findByTokenAndExpiresAtAfter(eq(token), any(Instant.class)))
                .thenReturn(Optional.of(verificationToken));

        //when
        VerificationToken result = verificationTokenService.consumeActiveVerificationToken(token);

        //then
        assertThat(result).isEqualTo(verificationToken);
        verify(verificationTokenRepository).delete(verificationToken);
    }

    @Test
    void shouldThrowInvalidVerificationTokenException() {
        //given
        final String token = UUID.randomUUID().toString();
        when(verificationTokenRepository.findByTokenAndExpiresAtAfter(eq(token), any(Instant.class)))
                .thenReturn(Optional.empty());

        //when && then
        assertThatThrownBy(() -> verificationTokenService.consumeActiveVerificationToken(token))
                .isInstanceOf(UserManagementException.class)
                .hasMessage(ExceptionType.VERIFICATION_TOKEN_NOT_FOUND.getMessage());
    }
}
