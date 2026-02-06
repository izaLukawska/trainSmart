package org.lukawska.trainsmart.statements.application.validation;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementCommand;
import org.lukawska.trainsmart.statements.application.exception.ExceptionType;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;
import org.lukawska.trainsmart.statements.infra.config.Statement;
import org.lukawska.trainsmart.statements.infra.config.StatementsDefinition;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAgreementValidator {

    private final StatementsDefinition statementsDefinition;

    public Statement validateUserAgreement(UserAgreementCommand command) {
        Statement statement = getStatement(command);
        validateRequiredAcceptance(statement, command);

        return statement;
    }

    public boolean outdatedUserAgreement(UserAgreement userAgreement) {
        Statement requiredStatement = statementsDefinition.getRequiredStatementsMap()
                                                          .get(userAgreement.getStatementCode());

        return requiredStatement != null && userAgreement.getStatementVersion() != requiredStatement.version();
    }

    private Statement getStatement(UserAgreementCommand command) {
        return statementsDefinition.findStatementByCode(command.statementCode())
                                   .orElseThrow(() -> new StatementException(ExceptionType.STATEMENT_NOT_FOUND));
    }

    private void validateRequiredAcceptance(Statement statement, UserAgreementCommand command) {
        if (statement.required() && command.agreementStatus() == AgreementStatus.REJECTED) {
            throw new StatementException(ExceptionType.STATEMENT_ACCEPTANCE_REQUIRED);
        }
    }
}
