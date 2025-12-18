package org.lukawska.trainsmart.sharedpersistence.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.sharedpersistence.application.exception.UserNotFoundException;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.sharedpersistence.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAccessService {

    private final UserRepository userRepository;

    public User getUserById(Long userId) {
        log.info("Retrieving user for ID: {}", userId);
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    public User getUserByUsername(String username) {
        log.info("Retrieving user for username {}", username);
        return userRepository.findByUsername(username)
                             .orElseThrow(() -> new UserNotFoundException(username));
    }
}
