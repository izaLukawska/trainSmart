package org.lukawska.trainsmart.fileexport.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.fileexport.api.FileExportApi;
import org.lukawska.trainsmart.fileexport.application.dto.ExportedFileResponse;
import org.lukawska.trainsmart.fileexport.application.exception.ExceptionType;
import org.lukawska.trainsmart.fileexport.application.exception.FileExportException;
import org.lukawska.trainsmart.fileexport.application.service.FileExportService;
import org.lukawska.trainsmart.fileexport.model.ExportFormatEnum;
import org.lukawska.trainsmart.fileexport.model.ExportTrainingPlanRequest;
import org.lukawska.trainsmart.mailing.infrastructure.config.MailingProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FileExportController implements FileExportApi {

    private final FileExportService fileExportService;

    private final MailingProperties mailingProperties;

    @Override
    public ResponseEntity<Resource> downloadTrainingPlan(ExportTrainingPlanRequest request) {
        log.info("Received download file request for file type {} ", request.getExportFormat().name());
        ExportedFileResponse response = fileExportService.downloadFile(request);

        return ResponseEntity.ok()
                             .contentType(resolveMediaType(request.getExportFormat()))
                             .contentLength(response.content().length)
                             .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                                                                                        .filename(response.filename())
                                                                                        .build()
                                                                                        .toString())
                             .body(new ByteArrayResource(response.content()));
    }

    private MediaType resolveMediaType(ExportFormatEnum exportFormatEnum) {
        return MediaType.parseMediaType(
                Optional.ofNullable(mailingProperties.getValidMimeTypes().get(exportFormatEnum.name()))
                        .filter(type -> !type.isEmpty())
                        .map(List::getFirst)
                        .orElseThrow(() -> new FileExportException(ExceptionType.INVALID_MEDIA_TYPE)));
    }
}
