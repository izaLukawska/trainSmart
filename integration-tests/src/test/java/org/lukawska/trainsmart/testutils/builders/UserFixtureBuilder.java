package org.lukawska.trainsmart.testutils.builders;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.sharedpersistence.domain.valueObjects.Role;
import org.lukawska.trainsmart.usermanagement.domain.repository.UserRepository;

import java.time.LocalDate;
import java.util.UUID;

@RequiredArgsConstructor
public class UserFixtureBuilder {

    private final UserRepository userRepository;

    private String username = "active_user";

    private String email = UUID.randomUUID() + "@test.com";

    private Role role = Role.ROLE_USER;

    private LocalDate birthDate = LocalDate.of(1990, 3, 10);

    private String rawPassword = UUID.randomUUID().toString();

    private boolean activeAccount = true;

    public UserFixtureBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserFixtureBuilder withPassword(String password) {
        this.rawPassword = password;
        return this;
    }

    public UserFixtureBuilder inactive() {
        this.activeAccount = false;
        return this;
    }

    public User build() {
        User user = User.builder()
                        .username(username)
                        .email(email)
                        .role(role)
                        .birthDate(birthDate)
                        .password(rawPassword)
                        .build();

        if (activeAccount) {
            user.activate();
        }

        return user;
    }

    public User save() {
        return userRepository.save(build());
    }
}
