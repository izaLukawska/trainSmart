package org.lukawska.trainsmart.usermanagement.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.commons.jwt.JwtService;
import org.lukawska.trainsmart.security.auth.UserDetailsServiceImpl;
import org.lukawska.trainsmart.testutils.TestData;
import org.lukawska.trainsmart.usermanagement.application.service.UserService;
import org.lukawska.trainsmart.usermanagement.model.*;
import org.lukawska.trainsmart.usermanagement.model.SendVerificationLinkRequest.TokenTypeEnum;
import org.lukawska.trainsmart.usermanagement.model.UserProfileResponse.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.time.LocalDate;

import static org.lukawska.trainsmart.testutils.TestData.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@ActiveProfiles("test")
@WithMockUser
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
                                                          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void shouldReturn200WithUserProfileResponseWhenActivateAccount() throws Exception {
        //given
        final String token = TestData.tokenValue();
        final UserProfileResponse userProfileResponse = new UserProfileResponse(
                username(), email(), RoleEnum.ROLE_USER, false);
        when(userService.activateAccount(token)).thenReturn(userProfileResponse);

        //when
        RequestBuilder request = post("/users/activate").with(csrf())
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .param("token", token);

        //then
        mockMvc.perform(request)
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.username").value(userProfileResponse.getUsername()))
               .andExpect(jsonPath("$.email").value(userProfileResponse.getEmail()))
               .andExpect(jsonPath("$.role").value(userProfileResponse.getRole().name()))
               .andExpect(jsonPath("$.disabled").value(userProfileResponse.getDisabled()));
    }

    @Test
    void shouldReturn204WhenChangeEmail() throws Exception {
        //given
        final ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest(email(), email());
        doNothing().when(userService).changeEmail(changeEmailRequest);

        //when
        RequestBuilder request = patch("/users/me/change-email").with(csrf())
                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                .content(objectMapper.writeValueAsString(
                                                                        changeEmailRequest));

        //then
        mockMvc.perform(request)
               .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn204WhenChangePassword() throws Exception {
        //given
        final ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(rawPassword(), rawPassword());
        doNothing().when(userService).changePassword(changePasswordRequest);

        //when
        RequestBuilder request = patch("/users/me/change-password").with(csrf())
                                                                   .contentType(MediaType.APPLICATION_JSON)
                                                                   .content(objectMapper.writeValueAsString(
                                                                           changePasswordRequest));

        //then
        mockMvc.perform(request)
               .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn204WhenDeleteAccount() throws Exception {
        //given
        doNothing().when(userService).deleteAccount();

        //when
        RequestBuilder request = delete("/users/me/delete").with(csrf());

        //then
        mockMvc.perform(request)
               .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn201UserProfileResponseWhenRegister() throws Exception {
        //given
        final RegisterUserRequest registerUserRequest = new RegisterUserRequest(
                username(), rawPassword(), email(), RegisterUserRequest.RoleEnum.ROLE_ADMIN,
                LocalDate.of(1989, 4, 15));
        final UserProfileResponse userProfileResponse = new UserProfileResponse(registerUserRequest.getUsername(),
                                                                                registerUserRequest.getEmail(),
                                                                                RoleEnum.ROLE_ADMIN,
                                                                                true);
        when(userService.registerUser(registerUserRequest)).thenReturn(userProfileResponse);

        //when
        RequestBuilder request = post("/users/register").with(csrf())
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .content(objectMapper.writeValueAsString(registerUserRequest));

        //then
        mockMvc.perform(request)
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.username").value(userProfileResponse.getUsername()))
               .andExpect(jsonPath("$.email").value(userProfileResponse.getEmail()))
               .andExpect(jsonPath("$.role").value(userProfileResponse.getRole().name()))
               .andExpect(jsonPath("$.disabled").value(userProfileResponse.getDisabled()));
    }

    @Test
    void shouldReturn204WhenResetPassword() throws Exception {
        //given
        final ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(tokenValue(), rawPassword());
        doNothing().when(userService).resetPassword(resetPasswordRequest);

        //when
        RequestBuilder request = post("/users/password-reset").with(csrf())
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .content(objectMapper.writeValueAsString(
                                                                      resetPasswordRequest));
        //then
        mockMvc.perform(request)
               .andExpect(status().isOk());
    }

    @Test
    void shouldReturn204WhenSendVerificationLink() throws Exception {
        //given
        final SendVerificationLinkRequest sendVerificationLinkRequest = new SendVerificationLinkRequest(
                username(), TokenTypeEnum.ACTIVATION);
        doNothing().when(userService).sendVerificationLink(sendVerificationLinkRequest);

        //when
        RequestBuilder request = post("/users/send-verification-link").with(csrf())
                                                                      .contentType(MediaType.APPLICATION_JSON)
                                                                      .content(objectMapper.writeValueAsString(
                                                                              sendVerificationLinkRequest));

        //then
        mockMvc.perform(request)
               .andExpect(status().isAccepted());
    }
}
