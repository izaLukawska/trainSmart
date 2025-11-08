package org.lukawska.trainsmart.shared_persistence.application.service;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.shared_persistence.application.exception.UserNotFoundException;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.lukawska.trainsmart.shared_persistence.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                             .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
