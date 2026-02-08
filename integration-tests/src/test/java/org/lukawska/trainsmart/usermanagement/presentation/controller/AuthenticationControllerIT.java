package org.lukawska.trainsmart.usermanagement.presentation.controller;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.commons.jwt.JwtService;
import org.lukawska.trainsmart.security.auth.UserDetailsServiceImpl;
import org.lukawska.trainsmart.usermanagement.application.service.AuthenticationService;
import org.lukawska.trainsmart.usermanagement.model.AuthResponse;
import org.lukawska.trainsmart.usermanagement.model.LoginRequest;
import org.lukawska.trainsmart.usermanagement.model.LogoutRequest;
import org.lukawska.trainsmart.usermanagement.model.RefreshTokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.lukawska.trainsmart.testutils.TestData.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthenticationController.class)
@ActiveProfiles("test")
class AuthenticationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @WithMockUser
    @Test
    void shouldReturnAuthResponseWhenLogin() throws Exception {
        //given
        final LoginRequest loginRequest = new LoginRequest(username(), rawPassword());
        final AuthResponse response = authResponse();
        when(authenticationService.login(any(LoginRequest.class))).thenReturn(response);

        //when
        RequestBuilder requestBuilder = post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                                                           .content(objectMapper.writeValueAsString(loginRequest))
                                                           .with(csrf());
        //then
        mockMvc.perform(requestBuilder)
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.accessToken").value(response.getAccessToken()))
               .andExpect(jsonPath("$.refreshToken").value(response.getRefreshToken()));
    }

    @WithMockUser
    @Test
    void shouldReturnAuthResponseWhenRefreshToken() throws Exception {
        //given
        final RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(tokenValue());
        final AuthResponse response = authResponse();

        when(authenticationService.refreshToken(refreshTokenRequest)).thenReturn(response);
        when(jwtService.generateAccessToken(any())).thenReturn(response.getAccessToken());

        //when
        RequestBuilder request = post("/auth/refresh-token").contentType(MediaType.APPLICATION_JSON)
                                                            .with(csrf())
                                                            .content(objectMapper.writeValueAsString(
                                                                    refreshTokenRequest));

        //when && then
        mockMvc.perform(request)
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.accessToken").value(response.getAccessToken()))
               .andExpect(jsonPath("$.refreshToken").value(response.getRefreshToken()));
    }

    @WithMockUser
    @Test
    void shouldReturn204WhenLogout() throws Exception {
        //given
        final LogoutRequest logoutRequest = new LogoutRequest(tokenValue());
        doNothing().when(authenticationService).logout(logoutRequest);

        //when
        RequestBuilder request = post("/auth/logout").contentType(MediaType.APPLICATION_JSON)
                                                     .with(csrf())
                                                     .content(objectMapper.writeValueAsString(logoutRequest));
        //when && then
        mockMvc.perform(request)
               .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn401WhenRefreshToken() throws Exception {
        //given
        final RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(tokenValue());

        //when
        RequestBuilder request = post("/auth/refresh-token").contentType(MediaType.APPLICATION_JSON)
                                                            .with(csrf())
                                                            .content(objectMapper.writeValueAsString(
                                                                    refreshTokenRequest));

        //when && then
        mockMvc.perform(request)
               .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401WhenWrongPassword() throws Exception {
        //given
        final LoginRequest loginRequest = new LoginRequest(username(), rawPassword());
        when(authenticationService.login(any(LoginRequest.class))).thenThrow(BadCredentialsException.class);

        //when
        RequestBuilder request = post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                                                    .content(objectMapper.writeValueAsString(loginRequest))
                                                    .with(csrf());
        //when && then
        mockMvc.perform(request)
               .andExpect(status().isUnauthorized());
    }

    private AuthResponse authResponse() {
        return new AuthResponse(tokenValue(), tokenValue());
    }
}
