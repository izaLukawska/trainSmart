package org.lukawska.trainsmart.usermanagement.application.service.registration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.sharedpersistence.domain.repositories.UserRepository;
import org.lukawska.trainsmart.usermanagement.application.dto.user.request.RegisterUserRequest;
import org.lukawska.trainsmart.usermanagement.application.dto.user.response.UserProfileResponse;
import org.lukawska.trainsmart.usermanagement.application.exception.ExceptionType;
import org.lukawska.trainsmart.usermanagement.application.exception.UserException;
import org.lukawska.trainsmart.usermanagement.domain.entity.VerificationToken;
import org.lukawska.trainsmart.usermanagement.domain.valueObject.TokenType;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.lukawska.trainsmart.usermanagement.application.mapper.UserMapper.mapToProfileResponse;
import static org.lukawska.trainsmart.usermanagement.application.mapper.UserMapper.mapToUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final VerificationTokenService verificationTokenService;

    @Transactional
    public UserProfileResponse register(RegisterUserRequest registerUserRequest) {
        try {
            log.info("Registering user with username: {}", registerUserRequest.username());
            User newUser = mapToUser(registerUserRequest, passwordEncoder);
            User savedUser = userRepository.save(newUser);

            log.info("Saved user with ID {}", savedUser.getId());
            verificationTokenService.sendVerificationMail(savedUser, TokenType.ACTIVATION);

            return mapToProfileResponse(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new UserException(ExceptionType.USER_ALREADY_EXISTS);
        }
    }

    @Transactional
    public UserProfileResponse activateAccount(String token) {
        VerificationToken activationToken = verificationTokenService.consumeActiveActivationToken(token);
        User user = activationToken.getUser();

        log.info("Activating account for user {}", user.getId());
        user.activate();
        userRepository.save(user);
        log.info("Account status changed to: {}", user.getStatus());

        return mapToProfileResponse(user);
    }
}
