package org.lukawska.trainsmart.statements.domain.repositories;

import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAgreementRepository extends JpaRepository<UserAgreement, Long> {

	Optional<UserAgreement> findByUserIdAndStatementCode(Long userId, String statementCode);

	List<UserAgreement> findAllByUserId(Long userId);

}
