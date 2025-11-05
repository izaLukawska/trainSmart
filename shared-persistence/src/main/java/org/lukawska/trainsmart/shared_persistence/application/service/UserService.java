package org.lukawska.trainsmart.shared_persistence.application.service;


import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.shared_persistence.application.exception.ExceptionType;
import org.lukawska.trainsmart.shared_persistence.application.exception.UserException;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.lukawska.trainsmart.shared_persistence.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                             .orElseThrow(() -> new UserException(ExceptionType.USER_NOT_FOUND));
    }
}
