package org.lukawska.trainsmart.statements.presentation.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.commons.jwt.JwtService;
import org.lukawska.trainsmart.security.auth.UserDetailsServiceImpl;
import org.lukawska.trainsmart.statements.application.services.UserAgreementService;
import org.lukawska.trainsmart.statements.model.AgreementStatusEnum;
import org.lukawska.trainsmart.statements.model.UserAgreementRequest;
import org.lukawska.trainsmart.statements.model.UserAgreementResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.List;
import java.util.Random;

import static org.lukawska.trainsmart.statements.presentation.controller.UserAgreementResponseAssert.then;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserAgreementController.class)
@ActiveProfiles("test")
@WithMockUser
public class UserAgreementControllerIT {

    private final static String BASE_URL = "/users/{userId}/agreements/";

    private static final Long USER_ID = 1L;

    @MockitoBean
    private UserAgreementService userAgreementService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSignAgreementAndReturnOkStatus() throws Exception {
        //given
        final UserAgreementResponse response = userAgreementResponse("RODO");
        final UserAgreementRequest request = new UserAgreementRequest(
                response.getStatementCode(), response.getAgreementStatus());
        when(userAgreementService.signAgreement(USER_ID, request)).thenReturn(response);

        //when && then
        RequestBuilder requestBuilder = put(BASE_URL.concat("/sign"), USER_ID)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        mockMvc.perform(requestBuilder).andExpect(status().isOk());
    }

    @Test
    void shouldReturnUserAgreementResponses() throws Exception {
        //given
        final UserAgreementResponse response1 = userAgreementResponse("PESEL");
        final UserAgreementResponse response2 = userAgreementResponse("RODO");
        when(userAgreementService.getRequiredStatementsToSign(USER_ID)).thenReturn(List.of(response1, response2));

        when(userAgreementService.getRequiredStatementsToSign(USER_ID))
                .thenReturn(List.of(response1, response2));

        //when
        String content = mockMvc.perform(get(BASE_URL, USER_ID).with(csrf()))
                                .andReturn().getResponse().getContentAsString();

        //then
        List<UserAgreementResponse> actualResponse = objectMapper.readValue(content, new TypeReference<>() {});
        then(actualResponse.getFirst()).hasId(response1.getId())
                                       .hasStatementCode(response1.getStatementCode())
                                       .hasVersion(response1.getVersion())
                                       .hasStatus(response1.getAgreementStatus());
        then(actualResponse.getLast()).hasId(response2.getId())
                                      .hasStatementCode(response2.getStatementCode())
                                      .hasVersion(response2.getVersion())
                                      .hasStatus(response2.getAgreementStatus());
    }

    private UserAgreementResponse userAgreementResponse(String code) {
        return new UserAgreementResponse(new Random().nextLong(), code, 2, AgreementStatusEnum.ACCEPTED);
    }
}
