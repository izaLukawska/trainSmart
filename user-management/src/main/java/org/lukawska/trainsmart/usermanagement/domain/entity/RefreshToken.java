package org.lukawska.trainsmart.usermanagement.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens", uniqueConstraints = @UniqueConstraint(columnNames = {"token", "user_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RefreshToken {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @Column(nullable = false)
    private Instant expiresAt;

    private boolean revoked = false;

    public RefreshToken(String token, User user, Instant expiresAt, boolean revoked) {
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
    }

    public void markAsRevoked() {
        this.revoked = true;
    }
}
