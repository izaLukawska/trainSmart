package org.lukawska.trainsmart.usermanagement.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.mailing.application.dto.MailDetails;
import org.lukawska.trainsmart.mailing.application.service.MailService;
import org.lukawska.trainsmart.usermanagement.domain.entity.VerificationToken;
import org.lukawska.trainsmart.usermanagement.domain.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Import({PostgresTestConfig.class, TestFixtures.class})
@ActiveProfiles("test")
@Transactional
public class VerificationTokenServiceIT {

    @Autowired
    private VerificationTokenService verificationTokenService;

    @Autowired
    private TestFixtures testFixtures;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @MockitoBean
    private MailService mailService;

    private VerificationToken token;

    @BeforeEach
    void setUp() {
        token = testFixtures.verificationToken().save();
    }

    @Test
    void shouldSendVerificationMailWithToken() {
        //when
        verificationTokenService.sendVerificationMail(token.getUser(), token.getTokenType());

        //then
        List<VerificationToken> tokens = verificationTokenRepository.findAll();
        assertThat(tokens).hasSize(1);
        assertThat(tokens.getFirst().getId()).isNotEqualTo(token.getId());
        verify(mailService).sendMail(any(MailDetails.class));
    }

    @Test
    void shouldReturnVerificationTokenWhenConsumeActiveVerificationToken() {
        //when
        VerificationToken result = verificationTokenService.consumeActiveVerificationToken(token.getToken());

        //then
        assertThat(result.getId()).isEqualTo(token.getId());
        assertThat(verificationTokenRepository.findById(result.getId())).isNotPresent();
    }
}
