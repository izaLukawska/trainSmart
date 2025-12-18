package org.lukawska.trainsmart.usermanagement.application.service.user;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserAccessService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.sharedpersistence.domain.repositories.UserRepository;
import org.lukawska.trainsmart.usermanagement.application.dto.user.request.RegisterUserRequest;
import org.lukawska.trainsmart.usermanagement.application.dto.user.response.UserProfileResponse;
import org.lukawska.trainsmart.usermanagement.application.exception.ExceptionType;
import org.lukawska.trainsmart.usermanagement.application.exception.UserException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.lukawska.trainsmart.usermanagement.application.mapper.UserMapper.mapToProfileResponse;
import static org.lukawska.trainsmart.usermanagement.application.mapper.UserMapper.mapToUser;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ActivationService activationService;

    private final UserAccessService userAccessService;

    @Transactional
    public UserProfileResponse register(RegisterUserRequest registerUserRequest) {
        try {
            User newUser = mapToUser(registerUserRequest, passwordEncoder);
            User savedUser = userRepository.save(newUser);
            activationService.sendActivationMail(savedUser);

            return mapToProfileResponse(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new UserException(ExceptionType.USER_ALREADY_EXISTS);
        }
    }

    @Transactional
    public UserProfileResponse changeEmail(Long userId, String newEmail) {
        User user = userAccessService.getUserById(userId);
        user.changeEmail(newEmail);
        try {
            User updatedUser = userRepository.save(user);
            return mapToProfileResponse(updatedUser);
        } catch (DataIntegrityViolationException e) {
            throw new UserException(ExceptionType.EMAIL_TAKEN);
        }
    }
}
