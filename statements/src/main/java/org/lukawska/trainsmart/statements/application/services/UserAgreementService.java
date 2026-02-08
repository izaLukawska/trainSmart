package org.lukawska.trainsmart.statements.application.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserAccessService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.statements.application.mapper.UserAgreementMapper;
import org.lukawska.trainsmart.statements.application.validation.UserAgreementValidator;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.repositories.UserAgreementRepository;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;
import org.lukawska.trainsmart.statements.infra.config.Statement;
import org.lukawska.trainsmart.statements.model.UserAgreementRequest;
import org.lukawska.trainsmart.statements.model.UserAgreementResponse;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.lukawska.trainsmart.statements.application.mapper.UserAgreementMapper.mapToCommand;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAgreementService {

    private final UserAgreementRepository userAgreementRepository;

    private final UserAgreementValidator userAgreementValidator;

    private final UserAccessService userService;

    @Transactional
    public UserAgreementResponse signAgreement(Long userId, UserAgreementRequest request) {
        log.info("Validating statement with code: {}", request.getStatementCode());
        Statement statement = userAgreementValidator.validateUserAgreement(mapToCommand(request));

        UserAgreement userAgreement = userAgreementRepository
                .findByUserIdAndStatementCode(userId, request.getStatementCode())
                .map(existingUserAgreement -> updateUserAgreement(existingUserAgreement, request, statement))
                .orElseGet(() -> createUserAgreement(userId, request, statement));

        return UserAgreementMapper.mapToResponse(userAgreement);
    }

    public List<UserAgreementResponse> getRequiredStatementsToSign(Long userId) {
        User existingUser = userService.getUserById(userId);

        List<UserAgreementResponse> outdatedUserAgreements = userAgreementRepository
                .findAllByUserId(userId)
                .stream()
                .filter(userAgreementValidator::outdatedUserAgreement)
                .map(UserAgreementMapper::mapToResponse)
                .toList();

        log.debug("Found required statements to sign count: {} for user with ID: {}",
                  outdatedUserAgreements.size(), existingUser.getId());

        return outdatedUserAgreements;
    }

    private UserAgreement createUserAgreement(Long userId, UserAgreementRequest request, Statement statement) {
        User existingUser = userService.getUserById(userId);

        UserAgreement agreementRecord = new UserAgreement(existingUser, request.getStatementCode(), statement.version(),
                                                          AgreementStatus.valueOf(request.getAgreementStatus().name()));

        log.info("Saving user agreement with ID: {}", agreementRecord.getId());

        return userAgreementRepository.save(agreementRecord);
    }

    private UserAgreement updateUserAgreement(UserAgreement existingUserAgreement,
                                              UserAgreementRequest request,
                                              Statement statement) {
        log.info("Updating user agreement with ID: {}", existingUserAgreement.getId());

        existingUserAgreement.changeStatus(AgreementStatus.valueOf(request.getAgreementStatus().name()));
        existingUserAgreement.updateStatementVersion(statement.version());

        return userAgreementRepository.save(existingUserAgreement);
    }
}
