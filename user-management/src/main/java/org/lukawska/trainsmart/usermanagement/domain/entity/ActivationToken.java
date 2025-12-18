package org.lukawska.trainsmart.usermanagement.domain.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;

import java.time.Instant;

@Entity
@Table(name = "activation_tokens")
@NoArgsConstructor
public class ActivationToken {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    public ActivationToken(String token, User user, Instant createdAt, Instant expiresAt) {
        this.token = token;
        this.user = user;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }
}

