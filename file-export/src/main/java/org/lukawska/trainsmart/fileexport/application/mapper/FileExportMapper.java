package org.lukawska.trainsmart.fileexport.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.fileexport.application.dto.ExportTrainingPlanCommand;
import org.lukawska.trainsmart.fileexport.application.dto.ExportedFileDto;
import org.lukawska.trainsmart.fileexport.application.dto.ExportedFileResponse;
import org.lukawska.trainsmart.fileexport.domain.export.ExportFormat;
import org.lukawska.trainsmart.fileexport.model.ExportTrainingPlanRequest;
import org.lukawska.trainsmart.mailing.domain.valueObjects.Attachment;
import org.springframework.http.MediaType;

@UtilityClass
public class FileExportMapper {

    public static ExportTrainingPlanCommand mapToDto(ExportTrainingPlanRequest request) {
        return new ExportTrainingPlanCommand(request.getPlanId(), request.getUserId(),
                                             ExportFormat.valueOf(request.getExportFormat().name()));
    }

    public static Attachment mapToAttachment(ExportedFileDto exportedFile) {
        return new Attachment(exportedFile.fileName(), exportedFile.content());
    }

    public static ExportedFileResponse mapToResponse(ExportedFileDto exportedFile, MediaType mediaType) {
        return new ExportedFileResponse(exportedFile.fileName(), exportedFile.content(), mediaType);
    }
}
