package org.lukawska.trainsmart.mailing.domain.repositories;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.mailing.domain.entities.MailEntity;
import org.lukawska.trainsmart.testutils.TestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
@Import({PostgresTestConfig.class, TestFixtures.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MailingRepositoryIT {

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private TestFixtures testFixtures;

    @Test
    void shouldReturnAllMailsForRecipient() {
        // given
        final MailEntity mailEntity1 = testFixtures.mail()
                                                   .save();
        final MailEntity mailEntity2 = testFixtures.mail()
                                                   .save();
        final MailEntity mailEntity3 = testFixtures.mail()
                                                   .withRecipients(List.of(TestData.email()))
                                                   .save();
        String recipient = mailEntity1.getRecipients().getFirst();

        //when
        List<MailEntity> result = mailRepository.findAllByRecipient(recipient);

        //then
        assertThat(result).hasSize(2);
        assertThat(result).containsAll(List.of(mailEntity1, mailEntity2));
        assertThat(result).doesNotContain(mailEntity3);
    }

    @Test
    void shouldThrowExceptionWhenSubjectIsNull() {
        //given
        final MailEntity mailWithoutSubject = testFixtures.mail()
                                                          .withSubject(null)
                                                          .build();

        //when && then
        assertThatThrownBy(() -> mailRepository.saveAndFlush(mailWithoutSubject))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
