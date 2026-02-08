package org.lukawska.trainsmart.sharedpersistence.domain.entities;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void shouldActivateUser() {
        //given
        final User user = User.builder().username("test").build();

        //when
        user.activate();

        //then
        assertThat(user.isDisabled()).isFalse();
    }

    @Test
    void shouldChangeEmail() {
        //given
        final User user = User.builder().email("old@email.com").build();
        final String newEmail = "newEmail@email.com";

        //when
        user.changeEmail(newEmail);

        //then
        assertThat(user.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void shouldEncodePasswordWhenChanging() {
        //given
        final User user = User.builder().password("old").build();
        final String newPassword = UUID.randomUUID().toString();

        //when
        user.changePassword(newPassword, passwordEncoder);

        //then
        assertThat(passwordEncoder.matches(newPassword, user.getPassword())).isTrue();
    }
}
