package org.lukawska.trainsmart.sharedpersistence.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.lukawska.trainsmart.sharedpersistence.domain.valueObjects.Role;
import org.lukawska.trainsmart.sharedpersistence.infrastructure.audit.AuditableEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Entity
@Getter
@ToString(exclude = {"password"})
public class User extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @JsonIgnore
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean disabled = true;

    private LocalDate birthDate;

    @Builder
    private User(String username, String password, String email, Role role, LocalDate birthDate) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.birthDate = birthDate;
    }

    public void changePassword(String newPassword, PasswordEncoder encoder) {
        this.password = encoder.encode(newPassword);
    }

    public void changeEmail(String newEmail) {
        this.email = newEmail;
    }

    public void activate() {
        this.disabled = false;
    }
}
