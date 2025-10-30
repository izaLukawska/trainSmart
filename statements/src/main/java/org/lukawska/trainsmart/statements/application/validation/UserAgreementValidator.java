package org.lukawska.trainsmart.statements.application.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.lukawska.trainsmart.shared_persistence.domain.repositories.UserRepository;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.application.exception.ExceptionType;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;
import org.lukawska.trainsmart.statements.infra.config.Statement;
import org.lukawska.trainsmart.statements.infra.config.StatementsDefinition;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserAgreementValidator {

    private final UserRepository userRepository;

    private final StatementsDefinition statementsDefinition;

    public Statement validateStatement(UserAgreementRequest request) {
        Statement statement = validateAndGetStatement(request);
        validateRequiredAcceptance(statement.required(), request);
        return statement;
    }

    public User validateAndGetUser(UserAgreementRequest request) {
        log.debug("Fetching user with ID: {}", request.userId());
        return userRepository.findById(request.userId())
                             .orElseThrow(() -> new StatementException(ExceptionType.USER_NOT_FOUND));
    }

    public void validateUserExistence(Long userId) {
        log.debug("Checking if user with ID: {} exists.", userId);
        userRepository.findById(userId)
                      .orElseThrow(() -> new StatementException(ExceptionType.USER_NOT_FOUND));
    }

    private Statement validateAndGetStatement(UserAgreementRequest request) {
        log.debug("Fetching data for statement with code: {}", request.statementCode());
        return statementsDefinition.findStatementByCode(request.statementCode())
                                   .orElseThrow(() -> new StatementException(ExceptionType.STATEMENT_NOT_FOUND));
    }

    private void validateRequiredAcceptance(boolean required, UserAgreementRequest request) {
        log.debug("Checking if required statement with code: {} is accepted", request.statementCode());
        if (required && request.agreementStatus() == AgreementStatus.REJECTED) {
            throw new StatementException(ExceptionType.STATEMENT_ACCEPTANCE_REQUIRED);
        }
    }
}
