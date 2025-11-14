package org.lukawska.trainsmart.mailing.domain.repository;

import org.lukawska.trainsmart.mailing.domain.entities.MailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MailRepository extends JpaRepository<MailEntity, Long> {

    @Query("SELECT m FROM MailEntity m JOIN m.recipients r WHERE r = :recipient")
    List<MailEntity> findAllByRecipient(@Param("recipient") String recipient);

    List<MailEntity> findAllBySubjectContaining(String keyword);

}
