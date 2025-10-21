package org.lukawska.trainsmart.shared_persistence.domain.repositories;

import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {}
