package org.lukawska.trainsmart.sharedpersistence.domain.repositories;

import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}
