package org.lukawska.trainsmart.statements.presentation.exception;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.springframework.http.ProblemDetail;

import java.net.URI;

@UtilityClass
class ProblemDetailMapper {

    static ProblemDetail toProblemDetail(ProblemType problemType) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(problemType.getStatus());
        problemDetail.setTitle(problemType.getTitle());
        problemDetail.setType(URI.create(problemType.getTypeUri()));

        return problemDetail;
    }

    static ProblemDetail toProblemDetailWithDetail(ProblemType problemType, String detail) {
        ProblemDetail problemDetail = toProblemDetail(problemType);
        problemDetail.setDetail(detail);

        return problemDetail;
    }

    static ProblemDetail toProblemDetail(StatementException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(exception.getExceptionType().getStatus(),
                                                                       exception.getMessage());

        String path = "/errors/" + toKebabCase(exception.getExceptionType().name());
        problemDetail.setType(URI.create(path));
        problemDetail.setTitle("Statement exception");
        return problemDetail;
    }

    private static String toKebabCase(String name) {
        return name.toLowerCase().replace('_', '-');
    }
}
