package org.lukawska.trainsmart.mailing.domain.repository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.commons.jwt.JwtService;
import org.lukawska.trainsmart.config.PostgresTestBase;
import org.lukawska.trainsmart.mailing.domain.entities.MailEntity;
import org.lukawska.trainsmart.mailing.testutil.MailingTestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.mailing.testutil.MailingTestData.mailWithRecipient;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MailingRepositoryIT extends PostgresTestBase {

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private MailRepository mailRepository;

    @Test
    void shouldReturnAllMailsForRecipient() {
        // given
        final String recipient = "test@test.com";
        mailRepository.saveAllAndFlush(List.of(
                mailWithRecipient(recipient), mailWithRecipient(recipient), mailWithRecipient("another@test.com")));

        //when
        List<MailEntity> result = mailRepository.findAllByRecipient(recipient);

        //then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(MailEntity::getRecipients)
                          .allMatch(recipients -> recipients.contains(recipient));
    }

    @Test
    void shouldThrowExceptionWhenSubjectIsNull() {
        //given
        final MailEntity mailWithoutSubject = MailingTestData.mailWithSubject(null);

        //when && then
        assertThatThrownBy(() -> mailRepository.saveAndFlush(mailWithoutSubject))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
