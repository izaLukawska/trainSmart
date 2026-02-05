package org.lukawska.trainsmart.usermanagement.domain.repository;

import org.lukawska.trainsmart.sharedpersistence.domain.repositories.UserAccessRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends UserAccessRepository {

    void deleteByUsername(String username);

}
