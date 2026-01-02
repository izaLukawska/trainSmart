package org.lukawska.trainsmart.usermanagement.domain.repository;

import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.domain.entity.VerificationToken;
import org.lukawska.trainsmart.usermanagement.domain.valueObject.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByTokenAndExpiresAtAfter(String token, Instant expiresAtAfter);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM VerificationToken vt  WHERE vt.user = :user AND vt.tokenType = :type")
    void deleteByUserAndTokenType(User user, TokenType type);

}
