package org.lukawska.trainsmart.fileexport.infrastructure.export;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.lukawska.trainsmart.fileexport.domain.export.ExportFormat;
import org.lukawska.trainsmart.fileexport.infrastrucutre.export.PdfTrainingPlanGenerator;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanDetails;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PdfTrainingPlanGeneratorTest {

    private final PdfTrainingPlanGenerator pdfTrainingPlanGenerator = new PdfTrainingPlanGenerator();

    @ParameterizedTest
    @EnumSource(value = ExportFormat.class, names = {"PDF"})
    void shouldReturnTrueWhenSupports(ExportFormat exportFormat) {
        //when
        boolean supports = pdfTrainingPlanGenerator.supports(exportFormat);

        //then
        assertThat(supports).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = ExportFormat.class, mode = EnumSource.Mode.EXCLUDE, names = {"PDF"})
    void shouldReturnFalseWhenSupports(ExportFormat exportFormat) {
        //when
        boolean supports = pdfTrainingPlanGenerator.supports(exportFormat);

        //then
        assertThat(supports).isFalse();
    }

    @Test
    void shouldReturnFileContentBytesWhenGenerate() {
        //given
        final TrainingPlanDetails trainingPlanDetails = FileExportTestData.trainingPlanDetails();

        //when
        byte[] result = pdfTrainingPlanGenerator.generate(trainingPlanDetails);

        //then
        assertThat(result).isNotEmpty();
    }
}
