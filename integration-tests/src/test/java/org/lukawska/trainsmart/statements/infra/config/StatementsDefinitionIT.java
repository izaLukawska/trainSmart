package org.lukawska.trainsmart.statements.infra.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import({PostgresTestConfig.class, TestFixtures.class})
@Transactional
class StatementsDefinitionIT {

    @Autowired
    private StatementsDefinition statementsDefinition;

    @Test
    void shouldLoadStatementsFromYaml() {
        //when
        Map<@NotBlank String, @NotNull Statement> statements = statementsDefinition.getStatements();

        //then
        Statement peselStatement = statements.get("PESEL");
        assertThat(peselStatement.title()).isEqualTo("PESEL Consent");
        assertThat(peselStatement.version()).isEqualTo(5);
        assertThat(peselStatement.required()).isTrue();
        assertThat(peselStatement.content()).isEqualTo("Example Content");

        Statement rodoStatement = statements.get("RODO");
        assertThat(rodoStatement.title()).isEqualTo("RODO Consent");
        assertThat(rodoStatement.version()).isEqualTo(3);
        assertThat(rodoStatement.required()).isTrue();
        assertThat(rodoStatement.content()).isEqualTo("Example Content");

        Statement marketingStatement = statements.get("MARKETING");
        assertThat(marketingStatement.title()).isEqualTo("MARKETING Consent");
        assertThat(marketingStatement.version()).isEqualTo(8);
        assertThat(marketingStatement.required()).isFalse();
        assertThat(marketingStatement.content()).isEqualTo("Example Content");
    }

    @Test
    void shouldReturnExistingStatementByCode() {
        //given
        final String code = "RODO";

        //when
        Optional<Statement> foundStatement = statementsDefinition.findStatementByCode(code);

        //then
        assertThat(foundStatement.isPresent()).isTrue();
        Statement statement = foundStatement.get();
        assertThat(statement.title()).isEqualTo("RODO Consent");
    }

    @Test
    void shouldReturnEmptyStatementWhenFindStatementByCode() {
        //given
        final String code = "invalid";

        //when
        Optional<Statement> foundStatement = statementsDefinition.findStatementByCode(code);

        //then
        assertThat(foundStatement).isEmpty();
    }

    @Test
    void shouldReturnOnlyRequiredStatements() {
        //when
        Map<String, Statement> required = statementsDefinition.getRequiredStatementsMap();

        //then
        assertThat(required).containsKey("PESEL");
        assertThat(required).containsKey("RODO");
        assertThat(required).doesNotContainKey("MARKETING");
    }
}
