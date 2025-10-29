package org.lukawska.trainsmart.openai.infra.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lukawska.trainsmart.openai.domain.port.ChatClientPort;
import org.lukawska.trainsmart.openai.infra.dto.ChatRolesRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenAiAdapter implements ChatClientPort<ChatRolesRequest, String> {

    private final ChatClient chatClient;

    @Override
    public String sendPrompt(ChatRolesRequest request) {
        ChatClient.ChatClientRequestSpec prompt = chatClient.prompt();

        if (StringUtils.isNotBlank(request.systemPrompt())) {
            prompt.system(request.userPrompt());
        }

        return prompt.user(request.userPrompt()).call().content();
    }
}
