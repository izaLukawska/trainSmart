package org.lukawska.trainsmart.statements.infra.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@ConfigurationProperties
@Getter
@Setter
@Validated
@Slf4j
public class StatementsDefinition {

    private final Map<@NotBlank String, @NotNull Statement> statements = new HashMap<>();

    public Optional<Statement> findStatementByCode(String code) {
        return Optional.ofNullable(statements.get(code));
    }

    public Map<String, Statement> getRequiredStatementsMap() {
        return statements.entrySet()
                         .stream()
                         .filter(s -> s.getValue().required())
                         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
