package org.lukawska.trainsmart.statements.infra.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "")
@Getter
@Setter
public class StatementsDefinitions {

	private Map<String, Statement> statements;

}
