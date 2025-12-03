package org.lukawska.trainsmart.openai.domain.port;

public interface ChatClientPort<I, O> {

    O sendPrompt(I input);

}
