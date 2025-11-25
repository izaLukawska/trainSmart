package org.lukawska.trainsmart.sharedpersistence.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.sharedpersistence.application.exception.UserNotFoundException;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.sharedpersistence.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public User getUserById(Long id) {
        log.info("Fetching user with ID: {}", id);
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
}
