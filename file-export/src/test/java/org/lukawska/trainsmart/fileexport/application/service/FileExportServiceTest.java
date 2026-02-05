package org.lukawska.trainsmart.fileexport.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.fileexport.application.dto.EmailExcelExportCommand;
import org.lukawska.trainsmart.fileexport.application.dto.ExportedFileResponse;
import org.lukawska.trainsmart.fileexport.application.resolver.DocumentGeneratorResolver;
import org.lukawska.trainsmart.fileexport.domain.export.DocumentGenerator;
import org.lukawska.trainsmart.fileexport.domain.export.ExportFormat;
import org.lukawska.trainsmart.fileexport.model.ExportFormatEnum;
import org.lukawska.trainsmart.fileexport.model.ExportTrainingPlanRequest;
import org.lukawska.trainsmart.mailing.application.service.MailService;
import org.lukawska.trainsmart.mailing.infrastructure.config.MailingProperties;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanDetails;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingWeekDetails;
import org.lukawska.trainsmart.trainingplan.application.service.TrainingPlanService;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileExportServiceTest {

    private static final Long PLAN_ID = 1L;

    private static final Long USER_ID = 2L;

    private static final byte[] CONTENT = new byte[]{67, 13};

    @Mock
    private TrainingPlanService trainingPlanService;

    @Mock
    private MailService mailService;

    @Mock
    private DocumentGeneratorResolver documentGeneratorResolver;

    @Mock
    private MailingProperties mailingProperties;

    @InjectMocks
    private FileExportService fileExportService;

    @Test
    void shouldReturnExportedFileResponseWhenDownloadFile() {
        //given
        final ExportFormat exportFormat = ExportFormat.PDF;
        final DocumentGenerator documentGenerator = mock(DocumentGenerator.class);
        final ExportTrainingPlanRequest request = new ExportTrainingPlanRequest(PLAN_ID, USER_ID,
                                                                                ExportFormatEnum.PDF);
        final TrainingPlanDetails trainingPlanDetails = trainingPlanDetails();
        when(mailingProperties.getValidMimeTypes()).thenReturn(
                Map.of("pdf", List.of("application/pdf")));
        when(trainingPlanService.getTrainingPlanDetailsByIdAndUserId(PLAN_ID, USER_ID))
                .thenReturn(trainingPlanDetails);
        when(documentGeneratorResolver.chooseStrategy(exportFormat)).thenReturn(documentGenerator);
        when(documentGenerator.generate(trainingPlanDetails)).thenReturn(CONTENT);

        //when
        ExportedFileResponse response = fileExportService.downloadFile(request);

        //then
        assertThat(response.filename()).endsWith(exportFormat.getExtension());
        assertThat(response.content()).isEqualTo(CONTENT);
    }

    @Test
    void shouldSendExcelTrainingPlanToEmail() {
        //given
        final DocumentGenerator documentGenerator = mock(DocumentGenerator.class);
        final String email = UUID.randomUUID() + "@example.com";
        final EmailExcelExportCommand command = new EmailExcelExportCommand(PLAN_ID, USER_ID, email);
        final TrainingPlanDetails trainingPlanDetails = trainingPlanDetails();

        when(trainingPlanService.getTrainingPlanDetailsByIdAndUserId(PLAN_ID, USER_ID)).thenReturn(trainingPlanDetails);
        when(documentGeneratorResolver.chooseStrategy(any())).thenReturn(documentGenerator);
        when(documentGenerator.generate(trainingPlanDetails)).thenReturn(CONTENT);

        //when
        fileExportService.sendExcelTrainingPlanToEmail(command);

        //then
        verify(mailService, times(1)).sendMail(any());
    }

    private TrainingPlanDetails trainingPlanDetails() {
        return new TrainingPlanDetails(TrainingType.STRENGTH,
                                       PlanDuration.FOUR_WEEKS,
                                       List.of(mock(TrainingWeekDetails.class)));
    }
}
