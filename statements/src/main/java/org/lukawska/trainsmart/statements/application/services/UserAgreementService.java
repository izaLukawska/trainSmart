package org.lukawska.trainsmart.statements.application.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.shared_persistence.application.service.UserService;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementResponse;
import org.lukawska.trainsmart.statements.application.mapper.UserAgreementMapper;
import org.lukawska.trainsmart.statements.application.validation.UserAgreementValidator;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.repositories.UserAgreementRepository;
import org.lukawska.trainsmart.statements.infra.config.Statement;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAgreementService {

    private final UserAgreementRepository userAgreementRepository;

    private final UserAgreementValidator userAgreementValidator;

    private final UserService userService;

    @Transactional
    public UserAgreementResponse signAgreement(UserAgreementRequest request) {
        Statement statement = userAgreementValidator.validateUserAgreement(request);

        Optional<UserAgreement> userAgreement =
                userAgreementRepository.findByUserIdAndStatementCode(request.userId(), request.statementCode());

        if (userAgreement.isPresent()) {
            UserAgreement existingUserAgreement = userAgreement.get();

            log.info("Updating user agreement with ID: {}", existingUserAgreement.getId());
            existingUserAgreement.changeStatus(request.agreementStatus());
            existingUserAgreement.updateStatementVersion(statement.version());

            return UserAgreementMapper.mapToResponse(userAgreementRepository.save(existingUserAgreement));
        } else {
            log.info("Checking if user with ID: {} exists", request.userId());
            User existingUser = userService.getUserById(request.userId());

            UserAgreement agreementRecord = new UserAgreement(existingUser,
                                                              request.statementCode(),
                                                              statement.version(),
                                                              request.agreementStatus());

            log.info("Saving agreement with user ID: {} and statement code: {}",
                     request.userId(), request.statementCode());

            return UserAgreementMapper.mapToResponse(userAgreementRepository.save(agreementRecord));
        }
    }

    public List<UserAgreementResponse> getRequiredStatementsToSign(Long userId) {
        User existingUser = userService.getUserById(userId);

        log.info("Getting required statements to sign for user with ID: {}", existingUser.getId());

        List<UserAgreementResponse> outdatedUserAgreements = userAgreementRepository
                .findAllByUserId(userId)
                .stream()
                .filter(userAgreementValidator::outdatedUserAgreement)
                .map(UserAgreementMapper::mapToResponse)
                .toList();

        log.debug("Found required statements to sign count: {} for user with ID: {}",
                  outdatedUserAgreements.size(), userId);

        return outdatedUserAgreements;
    }
}
