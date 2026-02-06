package org.lukawska.trainsmart.mailing.presentation.controller;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.commons.jwt.JwtService;
import org.lukawska.trainsmart.mailing.application.service.MailService;
import org.lukawska.trainsmart.mailing.model.MailResponse;
import org.lukawska.trainsmart.security.auth.UserDetailsServiceImpl;
import org.lukawska.trainsmart.testutils.TestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MailController.class)
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN")
class MailControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MailService mailService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldReturnMailById() throws Exception {
        // given
        final MailResponse mailResponse = mailResponse();
        final Long id = mailResponse.getId();
        when(mailService.getMailResponseById(id)).thenReturn(mailResponse);

        // when && then
        mockMvc.perform(get("/mail/" + id).with(csrf()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(mailResponse.getId()))
               .andExpect(jsonPath("$.subject").value(mailResponse.getSubject()))
               .andExpect(jsonPath("$.recipients").value(mailResponse.getRecipients().getFirst()));
    }

    @Test
    void shouldReturnAllMailsByRecipient() throws Exception {
        // given
        final MailResponse mailResponse = mailResponse();
        final List<MailResponse> mailResponses = List.of(mailResponse, mailResponse);
        final String recipient = mailResponses.getFirst().getRecipients().getFirst();
        when(mailService.getAllMailsByRecipient(recipient)).thenReturn(mailResponses);

        // when && then
        mockMvc.perform(get("/mail/recipient").with(csrf())
                                              .param("recipient", recipient))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(mailResponses.size()))
               .andExpect(jsonPath("$[*].recipients", everyItem(hasItem(recipient))));
    }

    @Test
    void shouldReturnAllMailsBySubject() throws Exception {
        // given
        final List<MailResponse> mailResponses = List.of(mailResponse(), mailResponse());
        final String keyword = mailResponses.getFirst().getSubject().substring(3);
        when(mailService.getAllMailsBySubjectContaining(keyword)).thenReturn(mailResponses);

        // when && then
        mockMvc.perform(get("/mail/subject").with(csrf())
                                            .param("keyword", keyword))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(mailResponses.size()))
               .andExpect(jsonPath("$[*].subject", everyItem(containsString(keyword))));
    }

    private MailResponse mailResponse() {
        return MailResponse.builder()
                           .id(20L)
                           .recipients(List.of(TestData.email()))
                           .sentAt(Instant.now())
                           .subject("subject")
                           .cc(List.of(TestData.email()))
                           .build();

    }
}
