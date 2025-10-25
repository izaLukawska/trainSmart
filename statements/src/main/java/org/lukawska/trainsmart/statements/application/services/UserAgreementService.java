package org.lukawska.trainsmart.statements.application.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementResponse;
import org.lukawska.trainsmart.statements.application.exception.ExceptionType;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.lukawska.trainsmart.statements.application.mapper.UserAgreementMapper;
import org.lukawska.trainsmart.statements.application.validation.UserAgreementValidator;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.repositories.UserAgreementRepository;
import org.lukawska.trainsmart.statements.infra.config.Statement;
import org.lukawska.trainsmart.statements.infra.config.StatementsDefinition;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAgreementService {

    private final UserAgreementRepository agreementRepository;

    private final UserAgreementValidator userAgreementValidator;

    private final StatementsDefinition statementsDefinition;

    @Transactional
    public UserAgreementResponse signNewAgreement(UserAgreementRequest request) {
        Statement statement = userAgreementValidator.validateStatement(request);
        User existingUser = userAgreementValidator.validateAndGetUser(request);

        UserAgreement agreementRecord = new UserAgreement(existingUser,
                                                          request.statementCode(),
                                                          statement.version(),
                                                          request.status());

        log.info("Saving agreement with user ID: {} and statement code: {}",
                 request.userId(), request.statementCode());
        return UserAgreementMapper.mapToResponse(agreementRepository.save(agreementRecord));
    }

    @Transactional
    public UserAgreementResponse reSignAgreement(UserAgreementRequest request) {
        Statement statement = userAgreementValidator.validateStatement(request);

        UserAgreement userAgreement = agreementRepository
                .findByUserIdAndStatementCode(request.userId(), request.statementCode())
                .orElseThrow(() -> new StatementException(ExceptionType.USER_AGREEMENT_NOT_FOUND));

        log.info("Updating version and changing status to: {} for agreement with ID: {}",
                 request.status(), userAgreement.getId());

        userAgreement.changeStatus(request.status());
        userAgreement.updateStatementVersion(statement.version());

        return UserAgreementMapper.mapToResponse(userAgreement);
    }

    public List<UserAgreementResponse> getRequiredStatementsToSign(Long userId) {
        log.debug("Getting required statements to sign for userId: {}", userId);
        List<UserAgreementResponse> outdatedUserAgreements = agreementRepository
                .findAllByUserId(userId)
                .stream()
                .filter(userAgreement -> {
                    Statement required = statementsDefinition
                            .getRequiredStatementsMap()
                            .get(userAgreement.getStatementCode());
                    return required != null &&
                            userAgreement.getStatementVersion() != required.version();
                })
                .map(UserAgreementMapper::mapToResponse)
                .toList();

        log.info("Found required statements to sign count: {} for user with ID: {}",
                 outdatedUserAgreements.size(), userId);
        return outdatedUserAgreements;
    }
}
