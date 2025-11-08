package org.lukawska.trainsmart.openai.infra.adapter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lukawska.trainsmart.openai.application.exception.OpenAiException;
import org.lukawska.trainsmart.openai.domain.port.ChatClientPort;
import org.lukawska.trainsmart.openai.infra.dto.ChatRolesRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@RequiredArgsConstructor
@Slf4j
@Validated
public class OpenAiAdapter implements ChatClientPort<ChatRolesRequest, String> {

    private final ChatClient chatClient;

    @Override
    public String sendPrompt(@Valid ChatRolesRequest request) {
        ChatClient.ChatClientRequestSpec prompt = chatClient.prompt();

        if (StringUtils.isNotBlank(request.systemPrompt())) {
            prompt.system(request.systemPrompt());
        }

        try {
            return prompt.user(request.userPrompt()).call().content();
        } catch (Exception e) {
            log.error("OpenAI call failed", e);
            throw new OpenAiException();
        }
    }
}
