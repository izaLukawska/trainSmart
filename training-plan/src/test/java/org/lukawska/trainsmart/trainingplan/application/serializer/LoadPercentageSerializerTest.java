package org.lukawska.trainsmart.trainingplan.application.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoadPercentageSerializerTest {

    private final LoadPercentageSerializer serializer = new LoadPercentageSerializer();

    @Mock
    JsonGenerator jsonGenerator;

    @Mock
    SerializerProvider serializerProvider;

    @ParameterizedTest
    @NullSource
    void shouldSerializeNullAsNull(Double value) throws IOException {
        //when
        serializer.serialize(value, jsonGenerator, serializerProvider);

        //then
        verify(jsonGenerator).writeNull();
    }

    @Test
    void shouldSerializeHalfUpValue() throws IOException {
        //when
        serializer.serialize(0.495, jsonGenerator, serializerProvider);

        //then
        verify(jsonGenerator).writeString("50%");
    }

    @Test
    void shouldSerializeHalfDownValue() throws IOException {
        //when
        serializer.serialize(0.494, jsonGenerator, serializerProvider);

        //then
        verify(jsonGenerator).writeString("49%");
    }
}
