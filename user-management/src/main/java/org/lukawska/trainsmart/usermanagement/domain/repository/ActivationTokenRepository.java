package org.lukawska.trainsmart.usermanagement.domain.repository;

import org.lukawska.trainsmart.usermanagement.domain.entity.ActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivationTokenRepository extends JpaRepository<ActivationToken, Long> {
}
