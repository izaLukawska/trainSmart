package org.lukawska.trainsmart.purchase.presentation.controller;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.commons.jwt.JwtService;
import org.lukawska.trainsmart.purchase.application.service.TrainingPlanPurchaseService;
import org.lukawska.trainsmart.purchase.model.*;
import org.lukawska.trainsmart.security.auth.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingPlanPurchaseController.class)
@ActiveProfiles("test")
@WithMockUser
public class TrainingPlanPurchaseControllerIT {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TrainingPlanPurchaseService purchaseService;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldReturn200WhenCheckoutPurchase() throws Exception {
        //given
        final PurchaseRequest purchaseRequest = PurchaseRequest.builder()
                                                               .planDuration(PlanDurationEnum.FOUR_WEEKS)
                                                               .trainingType(TrainingTypeEnum.STRENGTH)
                                                               .daysPerWeek(2).build();
        final CheckoutResponse checkoutResponse = CheckoutResponse.builder()
                                                                  .purchaseId(2L)
                                                                  .finalPrice(BigDecimal.TEN)
                                                                  .paymentCode("random")
                                                                  .build();
        when(purchaseService.purchaseCheckout(purchaseRequest)).thenReturn(checkoutResponse);

        //when && then
        mockMvc.perform(post("/purchase/checkout").with(csrf())
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .content(objectMapper.writeValueAsString(purchaseRequest)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.purchaseId").value(checkoutResponse.getPurchaseId()))
               .andExpect(jsonPath("$.finalPrice").value(checkoutResponse.getFinalPrice()));
    }

    @Test
    void shouldReturn201WhenCompletePurchase() throws Exception {
        //given
        final String paymentCode = UUID.randomUUID().toString();
        final PurchaseResponse purchaseResponse = PurchaseResponse.builder()
                                                                  .purchaseId(1L)
                                                                  .finalPrice(BigDecimal.TEN)
                                                                  .orderStatus(OrderStatusEnum.COMPLETED)
                                                                  .build();
        when(purchaseService.completePurchase(paymentCode)).thenReturn(purchaseResponse);

        //when && then
        mockMvc.perform(get("/purchase/complete").with(csrf())
                                                 .param("paymentCode", paymentCode))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.purchaseId").value(purchaseResponse.getPurchaseId()))
               .andExpect(jsonPath("finalPrice").value(10));
    }
}
