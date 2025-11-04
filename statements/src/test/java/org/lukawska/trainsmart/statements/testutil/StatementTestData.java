package org.lukawska.trainsmart.statements.testutil;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.statements.infra.config.Statement;

import java.util.Random;
import java.util.UUID;

@UtilityClass
public final class StatementTestData {

    public static Statement requiredStatement(int version) {
        return new Statement(UUID.randomUUID().toString(),
                             version,
                             true,
                             UUID.randomUUID().toString());
    }

    public static Statement requiredStatement() {
        return new Statement(UUID.randomUUID().toString(),
                             new Random().nextInt(10),
                             true,
                             UUID.randomUUID().toString());
    }

    public static Statement optionalStatement() {
        return new Statement(UUID.randomUUID().toString(),
                             new Random().nextInt(10),
                             false,
                             UUID.randomUUID().toString());
    }

    public static String randomStatementCode() {
        return UUID.randomUUID().toString();
    }
}
