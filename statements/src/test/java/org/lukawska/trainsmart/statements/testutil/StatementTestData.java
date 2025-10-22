package org.lukawska.trainsmart.statements.testutil;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.lukawska.trainsmart.statements.infra.config.Statement;

import java.util.Random;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StatementTestData {

    public static Statement randomStatement() {
        return new Statement(UUID.randomUUID().toString(),
                             new Random().nextInt(10),
                             new Random().nextBoolean(),
                             UUID.randomUUID().toString());
    }

    public static Statement statement(boolean required) {
        return new Statement(UUID.randomUUID().toString(),
                             new Random().nextInt(10),
                             required,
                             UUID.randomUUID().toString());
    }
}
