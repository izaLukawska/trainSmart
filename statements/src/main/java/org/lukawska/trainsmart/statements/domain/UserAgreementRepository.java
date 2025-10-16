package org.lukawska.trainsmart.statements.domain;

import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAgreementRepository extends JpaRepository<UserAgreement, Long> {}
