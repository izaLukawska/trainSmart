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
        Optional<Statement> found = statementsDefinition.findStatementByCode(statementCode);

        //then
        assertThat(found).isPresent().contains(statement);
    }

    @Test
    void shouldReturnOnlyRequiredStatementsMap() {
        // given
        final String statementCode = randomStatementCode();
        statementsDefinition = new StatementsDefinition();
        final String optionalStatementCode = randomStatementCode();
        final Statement requiredStatement = requiredStatement();
        Map<String, Statement> statementsDefinitionMap = Map.of(statementCode, requiredStatement,
                                                                optionalStatementCode, optionalStatement());
        statementsDefinition.getStatements().putAll(statementsDefinitionMap);

        // when
        Map<String, Statement> required = statementsDefinition.getRequiredStatementsMap();

        // then
        assertThat(required).hasSize(1)
                            .containsEntry(statementCode, requiredStatement)
                            .doesNotContainKey(optionalStatementCode);
    }

    @Test
    void shouldReturnEmptyRequiredStatementsMapWhenNoRequiredStatementsPresent() {
        // given
        statementsDefinition = new StatementsDefinition();
        Map<String, Statement> statementsDefinitionMap = Map.of(randomStatementCode(), optionalStatement());
        statementsDefinition.getStatements().putAll(statementsDefinitionMap);

        //when && then
        assertThat(statementsDefinition.getRequiredStatementsMap()).isEmpty();
    }
}
