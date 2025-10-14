package org.lukawska.trainSmart.mailing.domain.repository;

import org.lukawska.trainSmart.mailing.domain.entities.MailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MailRepository extends JpaRepository<MailEntity, Long> {

	@Query("SELECT m FROM MailEntity m JOIN m.recipients r WHERE r = :recipient")
	List<MailEntity> findAllByRecipient(@Param("recipient") String recipient);

	@Query("SELECT m FROM MailEntity m JOIN m.cc c WHERE c = :cc")
	List<MailEntity> findAllByCcContaining(@Param("cc") String cc);

	@Query("SELECT m FROM MailEntity m JOIN m.bcc b WHERE b = :bcc")
	List<MailEntity> findAllByBccContaining(@Param("bcc") String bcc);

	List<MailEntity> findAllBySentAtBetween(Instant from, Instant to);

	List<MailEntity> findAllBySubjectContaining(String keyword);

	List<MailEntity> findAllByIsHtml(boolean isHtml);

}
