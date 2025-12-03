package org.lukawska.trainsmart.sharedpersistence.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "users")
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String mail;

    public User(String username, String mail) {
        this.username = username;
        this.mail = mail;
    }
}
