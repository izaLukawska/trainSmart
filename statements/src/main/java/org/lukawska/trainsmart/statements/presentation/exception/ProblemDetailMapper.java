package org.lukawska.trainsmart.statements.presentation.exception;

import lombok.experimental.UtilityClass;
import org.springframework.http.ProblemDetail;

import java.net.URI;

@UtilityClass
class ProblemDetailMapper {

    static ProblemDetail toProblemDetailFromProblemType(ProblemType problemType) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(problemType.getStatus());
        problemDetail.setTitle(problemType.getTitle());
        problemDetail.setType(URI.create(problemType.getTypeUri()));

        if (problemType == ProblemType.INTERNAL_ERROR) {
            problemDetail.setDetail("Unexpected error occurred");
        }

        return problemDetail;
    }
}
