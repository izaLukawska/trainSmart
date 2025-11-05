package org.lukawska.trainsmart.shared_persistence.application.service;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.shared_persistence.application.exception.ExceptionType;
import org.lukawska.trainsmart.shared_persistence.application.exception.UserException;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.lukawska.trainsmart.shared_persistence.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public User getUserById(Long id) {
        return userRepository.findById(id)
                             .orElseThrow(() -> new UserException(ExceptionType.USER_NOT_FOUND));
    }
}
