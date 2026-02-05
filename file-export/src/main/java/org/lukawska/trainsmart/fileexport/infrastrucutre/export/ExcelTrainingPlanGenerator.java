package org.lukawska.trainsmart.fileexport.infrastrucutre.export;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.io.IOException;
import java.util.Optional;

import static org.lukawska.trainsmart.fileexport.infrastrucutre.export.ExcelStyleUtil.*;

@Component
@Slf4j
public class ExcelTrainingPlanGenerator implements DocumentGenerator {

    private static final String[] HEADERS = {"Weekday", "Exercise Name", "Reps", "Sets", "Intensity", "Load %"};

    @Override
    public boolean supports(ExportFormat exportFormat) {
        return exportFormat == ExportFormat.EXCEL;
    }

    @Override
    public byte[] generate(TrainingPlanDetails trainingPlan) {
        log.info("Generating EXCEL file");
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            CellStyle dayStyle = dayStyle(workbook);
            CellStyle headerStyle = headerStyle(workbook);
            CellStyle percentStyle = percentStyle(workbook);

            for (TrainingWeekDetails week : trainingPlan.trainingWeeks()) {
                log.debug("Creating sheet for week {} ({} blocks)", week.weekIndex(), week.trainingBlocks().size());
                Sheet sheet = workbook.createSheet("Week " + week.weekIndex());
                int rowIdx = 0;
                rowIdx = createHeaderRow(sheet, rowIdx, headerStyle);
                for (TrainingBlockDetails block : week.trainingBlocks()) {
                    int startRow = rowIdx;

                    for (BlockExerciseDetails ex : block.blockExercises()) {
                        rowIdx = createExerciseRow(sheet, rowIdx, ex, percentStyle);
                    }

                    createDayLabel(sheet, startRow, rowIdx - 1, block.assignedDay().name(), dayStyle);
                }
            }

            workbook.write(out);
            log.info("Finished generating Excel with size: {} bytes", out.size());
            return out.toByteArray();

        } catch (IOException e) {
            throw new FileExportException(ExceptionType.FILE_GENERATION_ERROR);
        }
    }

    private int createHeaderRow(Sheet sheet, int rowIdx, CellStyle style) {
        Row headerRow = sheet.createRow(rowIdx);

        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(style);
        }

        return rowIdx + 1;
    }

    private void createDayLabel(Sheet sheet, int startRow, int endRow, String dayName, CellStyle style) {
        Row dayRow = sheet.getRow(startRow);

        Cell dayCell = dayRow.createCell(0);
        dayCell.setCellValue(dayName);
        dayCell.setCellStyle(style);

        if (endRow > startRow) {
            sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, 0, 0));
        }

    }

    private int createExerciseRow(Sheet sheet, int rowIdx, BlockExerciseDetails blockExercise,
                                  CellStyle percentStyle) {
        Row row = sheet.createRow(rowIdx);
        Object[] values = {
                blockExercise.exerciseName(),
                blockExercise.reps(),
                blockExercise.sets(),
                blockExercise.intensity().name()
        };

        for (int i = 0; i < values.length; i++) {
            Cell cell = row.createCell(i + 1);
            if (values[i] instanceof Number n) {
                cell.setCellValue(n.doubleValue());
            } else {
                cell.setCellValue(String.valueOf(values[i]));
            }
        }

        Optional.ofNullable(blockExercise.loadPercent()).ifPresent(loadPercent -> {
            Cell loadCell = row.createCell(HEADERS.length - 1);
            loadCell.setCellValue(loadPercent);
            loadCell.setCellStyle(percentStyle);
        });

        return rowIdx + 1;
    }
}
