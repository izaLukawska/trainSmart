package org.lukawska.trainsmart.sharedpersistence.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.sharedpersistence.application.exception.UserNotFoundException;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.sharedpersistence.domain.repositories.UserAccessRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAccessServiceTest {

    private static final Long USER_ID = 2L;
    private static final String USERNAME = UUID.randomUUID().toString();
    @Mock
    private UserAccessRepository userRepository;
    @InjectMocks
    private UserAccessService userAccessService;

    @Test
    void shouldReturnUserWhenGetUserByIdFound() {
        //given
        final User expectedUser = mock(User.class);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(expectedUser));

        //when
        User result = userAccessService.getUserById(USER_ID);

        //then
        assertThat(result.getUsername()).isEqualTo(expectedUser.getUsername());
        assertThat(result.getRole()).isEqualTo(expectedUser.getRole());
        assertThat(result.getBirthDate()).isEqualTo(expectedUser.getBirthDate());
        assertThat(result.getEmail()).isEqualTo(expectedUser.getEmail());
        assertThat(result.isDisabled()).isEqualTo(expectedUser.isDisabled());
    }

    @Test
    void shouldReturnUserWhenGetUserByUsernameFound() {
        //given
        final User expectedUser = mock(User.class);
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(expectedUser));

        //when
        User result = userAccessService.getUserByUsername(USERNAME);

        //then
        assertThat(result.getUsername()).isEqualTo(expectedUser.getUsername());
        assertThat(result.getRole()).isEqualTo(expectedUser.getRole());
        assertThat(result.getBirthDate()).isEqualTo(expectedUser.getBirthDate());
        assertThat(result.getEmail()).isEqualTo(expectedUser.getEmail());
        assertThat(result.isDisabled()).isEqualTo(expectedUser.isDisabled());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenGetUserById() {
        //given
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> userAccessService.getUserById(USER_ID))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenGetUserByUsername() {
        //given
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        //when && then
        assertThatThrownBy(() -> userAccessService.getUserByUsername(USERNAME))
                .isInstanceOf(UserNotFoundException.class);

    }
}
