package org.lukawska.trainsmart.mailing.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.commons.jwt.JwtService;
import org.lukawska.trainsmart.mailing.application.dto.MailRequest;
import org.lukawska.trainsmart.mailing.application.dto.MailResponse;
import org.lukawska.trainsmart.mailing.application.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.lukawska.trainsmart.mailing.testutil.MailingTestData.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MailController.class)
@ActiveProfiles("test")
@WithMockUser
class MailControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MailService mailService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser(roles = "USER")
    void shouldCreateMailAndReturn201() throws Exception {
        // given
        final MailRequest mailRequest = mailRequestWithAttachments();
        final ObjectMapper objectMapper = new ObjectMapper();
        when(mailService.sendMail(mailRequest)).thenReturn(mailResponseWithId(1L));

        // when && then
        RequestBuilder request = post("/mail/send").with(csrf())
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content(objectMapper.writeValueAsString(mailRequest));

        mockMvc.perform(request)
               .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnMailById() throws Exception {
        // given
        final MailResponse mailResponse = mailResponseWithId(1L);
        when(mailService.getMailResponseById(1L)).thenReturn(mailResponse);

        // when && then
        mockMvc.perform(get("/mail/1").with(csrf()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(mailResponse.id()))
               .andExpect(jsonPath("$.subject").value(mailResponse.subject()))
               .andExpect(jsonPath("$.recipients[0]").value(mailResponse.recipients().getFirst()));
    }

    @Test
    void shouldReturnAllMailsByRecipient() throws Exception {
        // given
        final String recipient = "recipient@test.com";
        when(mailService.getAllMailsByRecipient(recipient))
                .thenReturn(List.of(mailResponseWithRecipient(recipient), mailResponseWithRecipient(recipient)));

        // when && then
        mockMvc.perform(get("/mail/recipient").with(csrf()).param("recipient", recipient))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[*].recipients", everyItem(hasItem(recipient))));
    }

    @Test
    void shouldReturnAllMailsBySubject() throws Exception {
        // given
        final String subject = "subject";
        final String keyword = "sub";
        when(mailService.getAllMailsBySubjectContaining(keyword))
                .thenReturn(List.of(mailResponseWithSubject(subject), mailResponseWithSubject(subject)));

        // when && then
        mockMvc.perform(get("/mail/subject").with(csrf()).param("keyword", keyword))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[*].subject", everyItem(containsString(keyword))));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidPathVariable() throws Exception {
        //when && then
        mockMvc.perform(get("/mail/-1").with(csrf())).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenInvalidRequestBody() throws Exception {
        //when && then
        mockMvc.perform(post("/mail/send").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{}"))
               .andExpect(status().isBadRequest());
    }
}
