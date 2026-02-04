package org.lukawska.trainsmart.fileexport.infrastrucutre.export;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.fileexport.application.exception.ExceptionType;
import org.lukawska.trainsmart.fileexport.application.exception.FileExportException;
import org.lukawska.trainsmart.fileexport.domain.export.DocumentGenerator;
import org.lukawska.trainsmart.fileexport.domain.export.ExportFormat;
import org.lukawska.trainsmart.trainingplan.application.dto.BlockExerciseDetails;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingBlockDetails;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanDetails;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingWeekDetails;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

@Component
@Slf4j
public class PdfTrainingPlanGenerator implements DocumentGenerator {

    private static final Font.FontFamily BASE_FONT = Font.FontFamily.HELVETICA;

    @Override
    public boolean supports(ExportFormat exportFormat) {
        return exportFormat == ExportFormat.PDF;
    }

    @Override
    public byte[] generate(TrainingPlanDetails trainingPlan) {
        log.info("Generating PDF file");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(BASE_FONT, 16, Font.BOLD);
            Font weekFont = new Font(BASE_FONT, 14, Font.BOLD);
            Font dayFont = new Font(BASE_FONT, 12);
            Font textFont = new Font(BASE_FONT, 11);

            document.add(new Paragraph(trainingPlan.trainingType().name() + "PROGRAM", titleFont));
            document.add(Chunk.NEWLINE);

            for (TrainingWeekDetails week : trainingPlan.trainingWeeks()) {
                log.debug("Writing week {} with {} blocks", week.weekIndex(), week.trainingBlocks().size());
                document.add(new Paragraph("Week " + week.weekIndex(), weekFont));
                document.add(Chunk.NEWLINE);

                for (TrainingBlockDetails day : week.trainingBlocks()) {
                    log.debug("Writing block for day {} with {} exercises",
                              day.assignedDay(), day.blockExercises().size());
                    document.add(new Paragraph(day.assignedDay().name(), dayFont));

                    for (BlockExerciseDetails blockExercise : day.blockExercises()) {
                        String data = computeExerciseData(blockExercise);
                        document.add(new Paragraph(data, textFont));
                    }
                    document.add(Chunk.NEWLINE);
                }
                document.add(Chunk.NEWLINE);
            }

        } catch (DocumentException e) {
            throw new FileExportException(ExceptionType.FILE_GENERATION_ERROR);
        } finally {
            document.close();
        }

        log.info("Finished generating PDF with size {} bytes", out.size());
        return out.toByteArray();
    }

    private String computeExerciseData(BlockExerciseDetails blockExercise) {
        String loadSuffix = Optional.ofNullable(blockExercise.loadPercent())
                                    .map(lp -> " (" + Math.round(lp * 100) + "% of 1RM)")
                                    .orElse("");

        return String.format("- %s %dx%d @ %s INTENSITY %s",
                             blockExercise.exerciseName(),
                             blockExercise.sets(),
                             blockExercise.reps(),
                             blockExercise.intensity().name(),
                             loadSuffix);
    }
}
