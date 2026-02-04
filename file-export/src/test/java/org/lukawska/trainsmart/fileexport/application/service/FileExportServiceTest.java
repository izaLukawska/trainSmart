package org.lukawska.trainsmart.fileexport.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.fileexport.application.dto.ExportTrainingPlanViaEmailCommand;
import org.lukawska.trainsmart.fileexport.application.resolver.DocumentGeneratorResolver;
import org.lukawska.trainsmart.fileexport.domain.export.DocumentGenerator;
import org.lukawska.trainsmart.fileexport.domain.export.ExportFormat;
import org.lukawska.trainsmart.fileexport.model.ExportFormatEnum;
import org.lukawska.trainsmart.fileexport.model.ExportTrainingPlanRequest;
import org.lukawska.trainsmart.mailing.application.service.MailService;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanDetails;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingWeekDetails;
import org.lukawska.trainsmart.trainingplan.application.service.TrainingPlanService;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileExportServiceTest {

    private static final Long PLAN_ID = 1L;

    private static final Long USER_ID = 2L;

    private static final ExportFormat EXPORT_FORMAT = ExportFormat.EXCEL;

    private static final byte[] CONTENT = new byte[]{67, 13};

    @Mock
    private TrainingPlanService trainingPlanService;

    @Mock
    private MailService mailService;

    @Mock
    private DocumentGeneratorResolver documentGeneratorResolver;

    @InjectMocks
    private FileExportService fileExportService;

    @Test
    void shouldReturnAttachmentWhenExportFile() throws IOException {
        //given
        final DocumentGenerator documentGenerator = mock(DocumentGenerator.class);
        final ExportTrainingPlanRequest request = new ExportTrainingPlanRequest(PLAN_ID, USER_ID,
                                                                                ExportFormatEnum.EXCEL);
        final TrainingPlanDetails trainingPlanDetails = trainingPlanDetails();
        when(trainingPlanService.getTrainingPlanDetailsByIdAndUserId(PLAN_ID, USER_ID))
                .thenReturn(trainingPlanDetails);
        when(documentGeneratorResolver.chooseStrategy(EXPORT_FORMAT)).thenReturn(documentGenerator);
        when(documentGenerator.generate(trainingPlanDetails)).thenReturn(CONTENT);

        //when
        Resource resource = fileExportService.downloadFile(request);

        //then
        assertThat(resource.getFilename()).endsWith(EXPORT_FORMAT.getExtension());
        assertThat(resource.getContentAsByteArray()).isEqualTo(CONTENT);
    }

    @Test
    void shouldSendTrainingPlanToEmail() {
        //given
        final DocumentGenerator documentGenerator = mock(DocumentGenerator.class);
        final ExportTrainingPlanViaEmailCommand command = new ExportTrainingPlanViaEmailCommand(
                PLAN_ID, USER_ID, EXPORT_FORMAT, UUID.randomUUID() + "@example.com");
        final TrainingPlanDetails trainingPlanDetails = trainingPlanDetails();

        when(trainingPlanService.getTrainingPlanDetailsByIdAndUserId(PLAN_ID, USER_ID)).thenReturn(trainingPlanDetails);
        when(documentGeneratorResolver.chooseStrategy(EXPORT_FORMAT)).thenReturn(documentGenerator);
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
