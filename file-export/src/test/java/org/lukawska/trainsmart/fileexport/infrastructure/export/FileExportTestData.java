package org.lukawska.trainsmart.fileexport.infrastructure.export;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.trainingplan.application.dto.BlockExerciseDetails;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingBlockDetails;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanDetails;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingWeekDetails;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.IntensityLevel;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;

import java.util.List;
import java.util.UUID;

@UtilityClass
class FileExportTestData {

    TrainingPlanDetails trainingPlanDetails() {
        return new TrainingPlanDetails(TrainingType.ENDURANCE, PlanDuration.EIGHT_WEEKS,
                                       List.of(trainingWeekDetails(trainingBlockDetails(blockExercisedetails()))));
    }

    TrainingWeekDetails trainingWeekDetails(TrainingBlockDetails trainingBlockDetails) {
        return new TrainingWeekDetails(3, List.of(trainingBlockDetails));
    }

    TrainingBlockDetails trainingBlockDetails(BlockExerciseDetails blockExercisedetails) {
        return new TrainingBlockDetails(WeekDay.TUESDAY, List.of(blockExercisedetails));
    }

    BlockExerciseDetails blockExercisedetails() {
        return new BlockExerciseDetails(UUID.randomUUID().toString(), 3, 5, IntensityLevel.MEDIUM, 0.89);
    }
}
