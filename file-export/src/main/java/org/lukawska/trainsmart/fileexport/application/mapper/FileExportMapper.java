package org.lukawska.trainsmart.fileexport.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.fileexport.application.dto.ExportTrainingPlanCommand;
import org.lukawska.trainsmart.fileexport.application.dto.ExportedFile;
import org.lukawska.trainsmart.fileexport.domain.export.ExportFormat;
import org.lukawska.trainsmart.fileexport.model.ExportTrainingPlanRequest;
import org.lukawska.trainsmart.mailing.domain.valueObjects.Attachment;

@UtilityClass
public class FileExportMapper {

    public static ExportTrainingPlanCommand mapToDto(ExportTrainingPlanRequest request) {
        return new ExportTrainingPlanCommand(request.getPlanId(), request.getUserId(),
                                             ExportFormat.valueOf(request.getExportFormat().name()));
    }

    public static Attachment mapToAttachment(ExportedFile exportedFile) {
        return new Attachment(exportedFile.fileName(), exportedFile.content());
    }

}
