package org.lukawska.trainsmart.sharedpersistence.domain.repositories;

import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {}
