package org.lukawska.trainsmart.statements.application.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.lukawska.trainsmart.shared_persistence.domain.repositories.UserRepository;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementResponse;
import org.lukawska.trainsmart.statements.application.exception.ExceptionType;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.lukawska.trainsmart.statements.application.mapper.UserAgreementMapper;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.repositories.UserAgreementRepository;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;
import org.lukawska.trainsmart.statements.infra.config.Statement;
import org.lukawska.trainsmart.statements.infra.config.StatementsDefinition;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAgreementService {

    private final UserAgreementRepository agreementRepository;

    private final UserRepository userRepository;

    private final StatementsDefinition definitions;

    @Transactional
    public UserAgreementResponse signNewAgreement(UserAgreementRequest request) {
        Statement statement = validateStatement(request);

        if (agreementRepository.findByUserIdAndStatementCode(request.userId(), request.statementCode())
                               .isPresent()) {
            throw new StatementException(ExceptionType.USER_AGREEMENT_ALREADY_EXISTS);
        }

        log.debug("Checking if user with ID {} exists.", request.userId());
        User user = userRepository.findById(request.userId())
                                  .orElseThrow(() -> new StatementException(ExceptionType.USER_NOT_FOUND));

        UserAgreement toSave = new UserAgreement(user,
                                                 request.statementCode(),
                                                 statement.version(),
                                                 request.status());

        log.info("Saving agreement with user ID: {} and statement code: {}",
                 request.userId(), request.statementCode());
        return UserAgreementMapper.mapToResponse(agreementRepository.save(toSave));
    }

    @Transactional
    public UserAgreementResponse reSignAgreement(UserAgreementRequest request) {
        Statement statement = validateStatement(request);

        UserAgreement ua = agreementRepository
                .findByUserIdAndStatementCode(request.userId(), request.statementCode())
                .orElseThrow(() -> new StatementException(ExceptionType.USER_AGREEMENT_NOT_FOUND));

        log.info("Updating version and changing status to: {} for agreement with ID: {}",
                 request.status(), ua.getId());

        ua.changeStatus(request.status());
        ua.updateStatementVersion(statement.version());

        return UserAgreementMapper.mapToResponse(ua);
    }

    public List<UserAgreementResponse> getRequiredStatementsToSign(Long userId) {
        log.debug("Getting required statements to sign for userId: {}", userId);
        List<UserAgreementResponse> found = agreementRepository.findAllByUserId(userId)
                                                               .stream()
                                                               .filter(ua -> {
                                                                   Statement required =
                                                                           definitions.getRequiredStatementsMap()
                                                                                      .get(ua.getStatementCode());
                                                                   return required != null &&
                                                                           ua.getStatementVersion() != required.version();
                                                               })
                                                               .map(UserAgreementMapper::mapToResponse)
                                                               .collect(Collectors.toList());

        log.info("Found required statements to sign count: {} for user with ID: {}", found.size(), userId);
        return found;
    }

    private Statement validateStatement(UserAgreementRequest request) {
        log.info("Fetching data for statement with code: {}", request.statementCode());
        Statement statement = definitions.findStatementByCode(request.statementCode())
                                         .orElseThrow(() -> new StatementException(ExceptionType.STATEMENT_NOT_FOUND));

        log.debug("Checking if required statement with code: {} is accepted", request.statementCode());
        if (statement.required() && request.status() == AgreementStatus.REJECTED) {
            throw new StatementException(ExceptionType.STATEMENT_ACCEPTANCE_REQUIRED);
        }

        return statement;
    }
}
