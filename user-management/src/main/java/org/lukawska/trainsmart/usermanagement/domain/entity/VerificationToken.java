package org.lukawska.trainsmart.usermanagement.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.domain.valueObject.TokenType;

import java.time.Instant;

@Entity
@Table(name = "verification_tokens")
@NoArgsConstructor
@Getter
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiresAt;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    public VerificationToken(String token, User user, Instant expiresAt, TokenType tokenType) {
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
        this.tokenType = tokenType;
    }

    public boolean isExpired() {
        return this.expiresAt.isBefore(Instant.now());
    }
}

