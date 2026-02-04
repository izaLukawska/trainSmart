package org.lukawska.trainsmart.fileexport.infrastrucutre.export;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.*;

@UtilityClass
public class ExcelStyleUtil {

    private static final String PERCENT_FORMAT = "0%";

    public CellStyle headerStyle(Workbook workbook) {
        return baseStyle(workbook);
    }

    public CellStyle dayStyle(Workbook workbook) {
        CellStyle style = baseStyle(workbook);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    public CellStyle percentStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat(PERCENT_FORMAT));
        return style;
    }

    private CellStyle baseStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFont(boldFont(workbook));
        style.setAlignment(HorizontalAlignment.CENTER);

        return style;
    }

    private Font boldFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        return font;
    }
}
