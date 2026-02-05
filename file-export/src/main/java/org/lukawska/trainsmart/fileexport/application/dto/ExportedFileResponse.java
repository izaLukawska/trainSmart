package org.lukawska.trainsmart.fileexport.application.dto;

public record ExportedFileResponse(String filename, byte[] content) {}
