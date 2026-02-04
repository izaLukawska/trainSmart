package org.lukawska.trainsmart.fileexport.application.dto;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

public record ExportFileResponse(Resource resource, MediaType mediaType) {}
