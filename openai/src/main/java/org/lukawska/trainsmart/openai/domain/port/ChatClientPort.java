package org.lukawska.trainsmart.openai.domain.port;

import jakarta.validation.Valid;

public interface ChatClientPort<I, O> {

    O sendPrompt(@Valid I input);

}
