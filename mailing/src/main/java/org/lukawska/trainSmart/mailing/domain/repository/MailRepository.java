package org.lukawska.trainSmart.mailing.domain.repository;

import org.lukawska.trainSmart.mailing.domain.entities.MailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailRepository extends JpaRepository<MailEntity, Long> {}
