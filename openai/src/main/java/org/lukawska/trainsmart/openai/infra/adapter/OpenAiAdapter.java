package org.lukawska.trainsmart.openai.infra.adapter;

import org.apache.commons.lang3.StringUtils;
import org.lukawska.trainsmart.openai.domain.port.ChatClientPort;
import org.lukawska.trainsmart.openai.infra.dto.ChatRolesRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class OpenAiAdapter implements ChatClientPort<ChatRolesRequest, String> {

    private final ChatClient chatClient;

    public OpenAiAdapter(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public String sendPrompt(ChatRolesRequest request) {
        ChatClient.ChatClientRequestSpec prompt = chatClient.prompt();

        if (StringUtils.isNotBlank(request.system())) {
            prompt.system(request.system());
        }

        return prompt.user(request.user())
                     .call()
                     .content();
    }
}
