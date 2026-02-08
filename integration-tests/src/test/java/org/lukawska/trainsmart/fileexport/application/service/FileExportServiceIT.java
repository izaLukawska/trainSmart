package org.lukawska.trainsmart.fileexport.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.fileexport.application.dto.EmailExcelExportCommand;
import org.lukawska.trainsmart.fileexport.application.dto.ExportedFileResponse;
import org.lukawska.trainsmart.fileexport.domain.export.ExportFormat;
import org.lukawska.trainsmart.fileexport.model.ExportFormatEnum;
import org.lukawska.trainsmart.fileexport.model.ExportTrainingPlanRequest;
import org.lukawska.trainsmart.mailing.application.dto.MailDetails;
import org.lukawska.trainsmart.mailing.application.service.MailService;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import({PostgresTestConfig.class, TestFixtures.class})
public class FileExportServiceIT {

    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private TestFixtures testFixtures;

    @MockitoBean
    private MailService mailService;

    private TrainingPlan trainingPlan;

    @BeforeEach
    void setUp() {
        trainingPlan = testFixtures.trainingPlan().save();
    }

    @Test
    void shouldSendExcelTrainingPlanToEmailSuccess() {
        //given
        final Long userId = trainingPlan.getUser().getId();
        final Long planId = trainingPlan.getId();
        final String email = trainingPlan.getUser().getEmail();
        final EmailExcelExportCommand command = new EmailExcelExportCommand(planId, userId, email);

        //when
        fileExportService.sendExcelTrainingPlanToEmail(command);

        //then
        verify(mailService).sendMail(any(MailDetails.class));
    }

    @Test
    void shouldReturnExportedFileResponseWhenDownloadFile() {
        //given
        final Long userId = trainingPlan.getUser().getId();
        final Long planId = trainingPlan.getId();
        final ExportFormat exportFormat = ExportFormat.PDF;
        final ExportTrainingPlanRequest request = new ExportTrainingPlanRequest(
                planId, userId, ExportFormatEnum.valueOf(exportFormat.name()));

        //when
        ExportedFileResponse response = fileExportService.downloadFile(request);

        //then
        assertThat(response.filename()).endsWith(exportFormat.getExtension());
        assertThat(response.content()).isNotNull();
    }
}
