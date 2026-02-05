package org.lukawska.trainsmart.fileexport.domain.export;

import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanDetails;

public interface DocumentGenerator {

    boolean supports(ExportFormat exportFormat);

    byte[] generate(TrainingPlanDetails trainingPlan);

}
