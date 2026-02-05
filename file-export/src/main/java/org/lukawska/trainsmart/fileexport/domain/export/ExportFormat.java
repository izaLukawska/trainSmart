package org.lukawska.trainsmart.fileexport.domain.export;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ExportFormat {

    PDF("pdf"), EXCEL("xlsx");

    private final String extension;

}
