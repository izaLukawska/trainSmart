package org.lukawska.trainsmart.fileexport.application.resolver;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.fileexport.application.exception.ExceptionType;
import org.lukawska.trainsmart.fileexport.application.exception.FileExportException;
import org.lukawska.trainsmart.fileexport.domain.export.DocumentGenerator;
import org.lukawska.trainsmart.fileexport.domain.export.ExportFormat;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentGeneratorResolver {

    private final List<DocumentGenerator> documentGenerators;

    public DocumentGenerator chooseStrategy(ExportFormat exportFormat) {
        return documentGenerators.stream()
                                 .filter(documentGenerator -> documentGenerator.supports(exportFormat))
                                 .findFirst()
                                 .orElseThrow(() -> new FileExportException(ExceptionType.INVALID_MEDIA_TYPE));
    }
}
