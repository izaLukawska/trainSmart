package org.lukawska.trainsmart.fileexport.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.fileexport.api.FileExportApi;
import org.lukawska.trainsmart.fileexport.application.exception.ExceptionType;
import org.lukawska.trainsmart.fileexport.application.exception.FileExportException;
import org.lukawska.trainsmart.fileexport.application.service.FileExportService;
import org.lukawska.trainsmart.fileexport.model.ExportFormatEnum;
import org.lukawska.trainsmart.fileexport.model.ExportTrainingPlanRequest;
import org.lukawska.trainsmart.mailing.infrastructure.config.MailingProperties;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FileExportController implements FileExportApi {

    private final FileExportService fileExportService;

    private final MailingProperties mailingProperties;

    @Override
    public ResponseEntity<Resource> downloadTrainingPlan(ExportTrainingPlanRequest request) {
        log.info("Received download file request for file type {} ", request.getExportFormat().name());
        Resource resource = fileExportService.downloadFile(request);
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                                                                  .filename(resource.getFilename(), UTF_8)
                                                                  .build();
        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                             .contentType(resolveMediaType(request.getExportFormat()))
                             .body(resource);
    }

    private MediaType resolveMediaType(ExportFormatEnum exportFormatEnum) {
        return MediaType.parseMediaType(
                Optional.ofNullable(mailingProperties.getValidMimeTypes().get(exportFormatEnum.name()))
                        .filter(type -> !type.isEmpty())
                        .map(List::getFirst)
                        .orElseThrow(() -> new FileExportException(ExceptionType.INVALID_MEDIA_TYPE)));
    }
}
