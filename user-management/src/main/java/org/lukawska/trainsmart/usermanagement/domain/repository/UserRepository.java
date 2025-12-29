package org.lukawska.trainsmart.usermanagement.domain.repository;

import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.sharedpersistence.domain.repositories.UserAccessRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends UserAccessRepository {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

}
