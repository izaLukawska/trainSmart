package org.lukawska.trainsmart.statements.infra.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lukawska.trainsmart.statements.testutil.StatementTestData.*;

@ExtendWith(MockitoExtension.class)
class StatementsDefinitionTest {

    private StatementsDefinition statementsDefinition;

    @Test
    void shouldReturnStatementWhenPresent() {
        //given
        final String statementCode = randomStatementCode();
        statementsDefinition = new StatementsDefinition();
        Statement statement = requiredStatement();
        statementsDefinition.getStatements().put(statementCode, statement);

        //when
        Optional<Statement> foundStatement = statementsDefinition.findStatementByCode(statementCode);

        //then
        assertThat(foundStatement).isPresent().contains(statement);
    }

    @Test
    void shouldReturnOnlyRequiredStatementsMap() {
        // given
        final String requiredStatementCode = randomStatementCode();
        statementsDefinition = new StatementsDefinition();
        final String optionalStatementCode = randomStatementCode();
        final Statement requiredStatement = requiredStatement();
        Map<String, Statement> statementsDefinitionMap = Map.of(requiredStatementCode, requiredStatement,
                                                                optionalStatementCode, optionalStatement());
        statementsDefinition.getStatements().putAll(statementsDefinitionMap);

        // when
        Map<String, Statement> requiredStatementsMap = statementsDefinition.getRequiredStatementsMap();

        // then
        assertThat(requiredStatementsMap).hasSize(1)
                                         .containsEntry(requiredStatementCode, requiredStatement)
                                         .doesNotContainKey(optionalStatementCode);
    }

    @Test
    void shouldReturnEmptyRequiredStatementsMapWhenNoRequiredStatementsPresent() {
        // given
        statementsDefinition = new StatementsDefinition();
        statementsDefinition.getStatements().put(randomStatementCode(), optionalStatement());

        //when && then
        assertThat(statementsDefinition.getRequiredStatementsMap()).isEmpty();
    }
}
