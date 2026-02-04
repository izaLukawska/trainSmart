package org.lukawska.trainsmart.fileexport.application.resolver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.fileexport.application.exception.ExceptionType;
import org.lukawska.trainsmart.fileexport.application.exception.FileExportException;
import org.lukawska.trainsmart.fileexport.domain.export.DocumentGenerator;
import org.lukawska.trainsmart.fileexport.domain.export.ExportFormat;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentGeneratorResolverTest {

    private final static ExportFormat exportFormat = ExportFormat.PDF;

    @Test
    void shouldReturnDocumentGeneratorWhenChooseStrategy() {
        //given
        final DocumentGenerator matchingStrategy = mock(DocumentGenerator.class);
        final DocumentGenerator notMatchingStrategy = mock(DocumentGenerator.class);
        final List<DocumentGenerator> strategies = List.of(matchingStrategy, notMatchingStrategy);
        final DocumentGeneratorResolver documentGeneratorResolver = new DocumentGeneratorResolver(strategies);

        when(matchingStrategy.supports(exportFormat)).thenReturn(true);

        //when
        DocumentGenerator result = documentGeneratorResolver.chooseStrategy(exportFormat);

        //then
        assertThat(result).isEqualTo(matchingStrategy);
        assertThat(result).isNotEqualTo(notMatchingStrategy);
    }

    @Test
    void shouldThrowFileExportExceptionWhenChooseStrategy() {
        //given
        final DocumentGenerator documentGenerator1 = mock(DocumentGenerator.class);
        final DocumentGenerator documentGenerator2 = mock(DocumentGenerator.class);
        final List<DocumentGenerator> strategies = List.of(documentGenerator1, documentGenerator2);
        final DocumentGeneratorResolver documentGeneratorResolver = new DocumentGeneratorResolver(strategies);

        //when && then
        assertThatThrownBy(() -> documentGeneratorResolver.chooseStrategy(exportFormat))
                .isInstanceOf(FileExportException.class)
                .hasMessage(ExceptionType.INVALID_MEDIA_TYPE.getMessage());
    }
}
