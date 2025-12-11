package org.lukawska.trainsmart.trainingplan.application.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class PercentSerializer extends StdSerializer<Double> {

    protected PercentSerializer() {
        super(Double.class);
    }

    @Override
    public void serialize(Double value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        if (value == null) {
            jsonGenerator.writeNull();
            return;
        }

        BigDecimal percentValue = BigDecimal.valueOf(value)
                                            .multiply(BigDecimal.valueOf(100))
                                            .setScale(0, RoundingMode.HALF_UP);
        jsonGenerator.writeString(percentValue.toPlainString() + "%");
    }
}
