package org.lukawska.trainsmart.fileexport.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.fileexport.application.dto.EmailExcelExportCommand;
import org.lukawska.trainsmart.fileexport.application.dto.ExportTrainingPlanCommand;
import org.lukawska.trainsmart.fileexport.application.dto.ExportedFileDto;
import org.lukawska.trainsmart.fileexport.application.dto.ExportedFileResponse;
import org.lukawska.trainsmart.fileexport.application.exception.ExceptionType;
import org.lukawska.trainsmart.fileexport.application.exception.FileExportException;
import org.lukawska.trainsmart.fileexport.application.mapper.FileExportMapper;
import org.lukawska.trainsmart.fileexport.application.resolver.DocumentGeneratorResolver;
import org.lukawska.trainsmart.fileexport.domain.export.DocumentGenerator;
import org.lukawska.trainsmart.fileexport.domain.export.ExportFormat;
import org.lukawska.trainsmart.fileexport.model.ExportFormatEnum;
import org.lukawska.trainsmart.fileexport.model.ExportTrainingPlanRequest;
import org.lukawska.trainsmart.mailing.application.dto.MailDetails;
import org.lukawska.trainsmart.mailing.application.service.MailService;
import org.lukawska.trainsmart.mailing.domain.valueObjects.Attachment;
import org.lukawska.trainsmart.mailing.infrastructure.config.MailingProperties;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanDetails;
import org.lukawska.trainsmart.trainingplan.application.service.TrainingPlanService;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileExportService {

    private final TrainingPlanService trainingPlanService;

    private final DocumentGeneratorResolver documentGeneratorResolver;

    private final MailService mailService;

    private final MailingProperties mailingProperties;

    private static final ExportFormat DEFAULT_EXPORT_FORMAT = ExportFormat.EXCEL;

    public ExportedFileResponse downloadFile(ExportTrainingPlanRequest request) {
        ExportTrainingPlanCommand command = FileExportMapper.mapToDto(request);
        return FileExportMapper.mapToResponse(exportFile(command), resolveMediaType(request.getExportFormat()));
    }

    @Transactional
    public void sendExcelTrainingPlanToEmail(EmailExcelExportCommand emailCommand) {
        ExportTrainingPlanCommand command = new ExportTrainingPlanCommand(
                emailCommand.planId(), emailCommand.userId(), DEFAULT_EXPORT_FORMAT);

        ExportedFileDto exportedFile = exportFile(command);
        Attachment attachment = FileExportMapper.mapToAttachment(exportedFile);

        MailDetails mailDetails = MailDetails.builder()
                                             .recipients(List.of(emailCommand.email()))
                                             .subject("Training plan")
                                             .text("You can find your generated plan in the attachment.")
                                             .attachments(List.of(attachment))
                                             .build();

        log.debug("Sending training plan to email");
        mailService.sendMail(mailDetails);
    }

    private ExportedFileDto exportFile(ExportTrainingPlanCommand command) {
        TrainingPlanDetails trainingPlanDetails = trainingPlanService.getTrainingPlanDetailsByIdAndUserId(
                command.planId(), command.userId());
        log.info("Generating file for training plan");

        ExportFormat exportFormat = command.exportFormat();
        DocumentGenerator documentGenerator = documentGeneratorResolver.chooseStrategy(exportFormat);
        byte[] content = documentGenerator.generate(trainingPlanDetails);
        String fileName = generateFileName(trainingPlanDetails.trainingType(), trainingPlanDetails.planDuration(),
                                           exportFormat);
        log.info("File generated {}", fileName);

        return new ExportedFileDto(fileName, content);
    }

    private String generateFileName(TrainingType trainingType, PlanDuration planDuration, ExportFormat exportFormat) {
        return trainingType.name().toLowerCase() + planDuration.getWeeksCount() + "." + exportFormat.getExtension();
    }

    private MediaType resolveMediaType(ExportFormatEnum exportFormatEnum) {
        String extension = ExportFormat.valueOf(exportFormatEnum.name()).getExtension();
        return MediaType.parseMediaType(
                Optional.ofNullable(mailingProperties.getValidMimeTypes().get(extension))
                        .filter(type -> !type.isEmpty())
                        .map(List::getFirst)
                        .orElseThrow(() -> new FileExportException(ExceptionType.INVALID_MEDIA_TYPE)));
    }
}
