package org.lukawska.trainsmart.fileexport.application.dto;

import org.springframework.http.MediaType;

public record ExportedFileResponse(String filename, byte[] content, MediaType mediaType) {}
