package org.lukawska.trainsmart.statements.infra.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.statements.testutil.StatementTestData;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StatementsDefinitionTest {

    private StatementsDefinition def;

    private String statementCode;

    @BeforeEach
    void setUp() {
        def = new StatementsDefinition();
        statementCode = UUID.randomUUID().toString();
    }

    @Test
    void shouldReturnStatementWhenPresent() {
        //given
        Statement statement = StatementTestData.randomStatement();
        Map<String, Statement> defMap = Map.of(statementCode, statement);
        def.setStatements(defMap);

        //when
        Optional<Statement> found = def.findStatementByCode(statementCode);

        //then
        assertThat(found).isPresent().contains(statement);
    }

    @Test
    void shouldReturnOnlyRequiredStatementsMap() {
        // given
        final String optionalStatementCode = UUID.randomUUID().toString();
        final Statement requiredStatement = StatementTestData.statement(true);
        final Statement optionalStatement = StatementTestData.statement(false);
        Map<String, Statement> defMap = Map.of(statementCode, requiredStatement,
                                               optionalStatementCode, optionalStatement);
        def.setStatements(defMap);

        // when
        Map<String, Statement> required = def.getRequiredStatementsMap();

        // then
        assertThat(required).hasSize(1)
                            .containsEntry(statementCode, requiredStatement)
                            .doesNotContainKey(optionalStatementCode);
    }

    @Test
    void shouldReturnEmptyRequiredStatementsMapWhenNoRequiredStatementsPresent() {
        // given
        Statement optionalStatement = StatementTestData.statement(false);
        Map<String, Statement> defMap = Map.of(statementCode, optionalStatement);
        def.setStatements(defMap);

        //when && then
        assertThat(def.getRequiredStatementsMap()).isEmpty();
    }
}
