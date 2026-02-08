package org.lukawska.trainsmart.sharedpersistence.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Import({PostgresTestConfig.class, TestFixtures.class})
public class UserAccessServiceIT {

    @Autowired
    private UserAccessService userAccessService;

    @Autowired
    private TestFixtures testFixtures;

    private User user;

    @BeforeEach
    void setUp() {
        user = testFixtures.user().save();
    }

    @Test
    void shouldReturnUserWhenGetUserByIdFound() {
        //given
        final Long userId = user.getId();

        //when
        User result = userAccessService.getUserById(userId);

        //then
        assertThat(result).isEqualTo(user);
    }

    @Test
    void shouldReturnUserWhenGetUserByUsernameFound() {
        //given
        final String username = user.getUsername();

        //when
        User result = userAccessService.getUserByUsername(username);

        //then
        assertThat(result).isEqualTo(user);
    }
}
