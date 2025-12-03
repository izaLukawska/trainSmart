package org.lukawska.trainsmart.openai.infra.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient openAiChatClient(ChatClient.Builder chatBuilder) {
        return chatBuilder.build();
    }
}
