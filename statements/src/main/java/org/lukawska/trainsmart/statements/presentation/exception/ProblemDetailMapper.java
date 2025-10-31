package org.lukawska.trainsmart.statements.presentation.exception;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.springframework.http.ProblemDetail;

import java.net.URI;

@UtilityClass
class ProblemDetailMapper {

    private static final String BASE_MESSAGE = "Unexpected error occurred";

    static ProblemDetail toProblemDetail(ProblemType problemType) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(problemType.getStatus());
        problemDetail.setTitle(problemType.getTitle());
        problemDetail.setType(URI.create(problemType.getTypeUri()));

        if (problemType == ProblemType.INTERNAL_ERROR) {
            problemDetail.setDetail(BASE_MESSAGE);
        }

        return problemDetail;
    }

    static ProblemDetail statementExceptionToProblemDetail(StatementException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(exception.getExceptionType().getStatus(),
                                                                       exception.getMessage());

        String path = "/errors/" + exception.getExceptionType().name().toLowerCase().replace('_', '-');
        problemDetail.setType(URI.create(path));
        problemDetail.setTitle("Statement exception");
        return problemDetail;
    }
}
