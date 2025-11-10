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

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAgreementService {

    private final UserAgreementRepository userAgreementRepository;

    private final UserAgreementValidator userAgreementValidator;

    private final UserService userService;

    @Transactional
    public UserAgreementResponse signAgreement(UserAgreementRequest request) {
        log.info("Validating statement with code: {}", request.statementCode());
        Statement statement = userAgreementValidator.validateUserAgreement(request);

        UserAgreement userAgreement = userAgreementRepository
                .findByUserIdAndStatementCode(request.userId(), request.statementCode())
                .map(existingUserAgreement -> updateUserAgreement(existingUserAgreement, request, statement))
                .orElseGet(() -> createUserAgreement(request, statement));

        return UserAgreementMapper.mapToResponse(userAgreement);
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

    private UserAgreement createUserAgreement(UserAgreementRequest request, Statement statement) {
        User existingUser = userService.getUserById(request.userId());

        UserAgreement agreementRecord = new UserAgreement(
                existingUser, request.statementCode(), statement.version(), request.agreementStatus());

        log.info("Saving user agreement with ID: {}", agreementRecord.getId());

        return userAgreementRepository.save(agreementRecord);
    }

    private UserAgreement updateUserAgreement(UserAgreement existingUserAgreement,
                                              UserAgreementRequest request,
                                              Statement statement) {
        log.info("Updating user agreement with ID: {}", existingUserAgreement.getId());

        existingUserAgreement.changeStatus(request.agreementStatus());
        existingUserAgreement.updateStatementVersion(statement.version());

        return userAgreementRepository.save(existingUserAgreement);
    }
}
