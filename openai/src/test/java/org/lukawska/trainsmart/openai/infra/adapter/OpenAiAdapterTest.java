package org.lukawska.trainsmart.openai.infra.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.openai.application.exception.ExceptionType;
import org.lukawska.trainsmart.openai.application.exception.OpenAiException;
import org.lukawska.trainsmart.openai.infra.dto.ChatRolesRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenAiAdapterTest {

    @Mock
    private ChatClient chatClient;

    @InjectMocks
    private OpenAiAdapter openAiAdapter;

    @Test
    void shouldSendPromptWithSystemPrompt() {
        //given
        final String systemPrompt = UUID.randomUUID().toString();
        final String userPrompt = UUID.randomUUID().toString();
        final String content = UUID.randomUUID().toString();

        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec callResponseSpec = mock(ChatClient.CallResponseSpec.class);

        when(chatClient.prompt()).thenReturn(requestSpec);
        doReturn(requestSpec).when(requestSpec).system(anyString());
        doReturn(requestSpec).when(requestSpec).user(userPrompt);
        doReturn(callResponseSpec).when(requestSpec).call();
        when(callResponseSpec.content()).thenReturn(content);

        //when
        String result = openAiAdapter.sendPrompt(new ChatRolesRequest(systemPrompt, userPrompt));

        //then
        assertThat(result).isEqualTo(content);
        verify(requestSpec).system(systemPrompt);
        verify(requestSpec).user(userPrompt);
    }

    @Test
    void shouldSendPromptWithoutSystemPrompt() {
        //given
        final String userPrompt = UUID.randomUUID().toString();
        final String content = UUID.randomUUID().toString();

        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec callResponseSpec = mock(ChatClient.CallResponseSpec.class);

        when(chatClient.prompt()).thenReturn(requestSpec);
        doReturn(requestSpec).when(requestSpec).user(userPrompt);
        doReturn(callResponseSpec).when(requestSpec).call();
        when(callResponseSpec.content()).thenReturn(content);

        //when
        String result = openAiAdapter.sendPrompt(new ChatRolesRequest(null, userPrompt));

        //then
        assertThat(result).isEqualTo(content);
        verify(requestSpec, never()).system(anyString());
        verify(requestSpec).user(userPrompt);
    }

    @Test
    void shouldThrowExceptionWhenSendPromptFail() {
        //given
        final String userPrompt = UUID.randomUUID().toString();
        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);

        when(chatClient.prompt()).thenReturn(requestSpec);
        doReturn(requestSpec).when(requestSpec).user(userPrompt);
        when(requestSpec.call()).thenThrow(new RuntimeException(UUID.randomUUID().toString()));

        //when && then
        assertThatThrownBy(() -> openAiAdapter.sendPrompt(new ChatRolesRequest(null, userPrompt)))
                .isInstanceOf(OpenAiException.class)
                .hasMessage(ExceptionType.OPENAI_CLIENT_ERROR.getMessage());
    }
}
