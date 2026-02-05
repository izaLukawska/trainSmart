package org.lukawska.trainsmart.purchase.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.fileexport.application.service.FileExportService;
import org.lukawska.trainsmart.purchase.application.exception.PurchaseException;
import org.lukawska.trainsmart.purchase.application.exception.PurchaseExceptionType;
import org.lukawska.trainsmart.purchase.domain.entity.TrainingPlanPurchase;
import org.lukawska.trainsmart.purchase.domain.repository.TrainingPlanPurchaseRepository;
import org.lukawska.trainsmart.purchase.domain.service.PricingCalculator;
import org.lukawska.trainsmart.purchase.model.CheckoutResponse;
import org.lukawska.trainsmart.purchase.model.OrderStatusEnum;
import org.lukawska.trainsmart.purchase.model.PurchaseRequest;
import org.lukawska.trainsmart.purchase.model.PurchaseResponse;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserAccessService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.exception.ExceptionType;
import org.lukawska.trainsmart.trainingplan.application.exception.TrainingPlanException;
import org.lukawska.trainsmart.trainingplan.application.service.TrainingPlanService;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.purchase.application.service.PurchaseTestData.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingPlanPurchaseServiceTest {

    @Mock
    private TrainingPlanPurchaseRepository purchaseRepository;

    @Mock
    private TrainingPlanService trainingPlanService;

    @Mock
    private UserAccessService userAccessService;

    @Mock
    private PricingCalculator pricingCalculator;

    @Mock
    private FileExportService fileExportService;

    @InjectMocks
    private TrainingPlanPurchaseService purchaseService;

    @Test
    void shouldReturnCheckoutResponseWhenPurchaseCheckout() {
        //given
        final BigDecimal expectedTotalPrice = BigDecimal.TEN;
        final TrainingPlanPurchase purchase = trainingPlanPurchase();
        final PlanDuration planDuration = purchase.getPlanDuration();
        purchase.assignTotalPrice(expectedTotalPrice);
        final PurchaseRequest request = purchaseRequest();
        final String username = username();

        mockAuthenticatedUser(username);
        when(userAccessService.getUserByUsername(username)).thenReturn(mock(User.class));
        when(pricingCalculator.calculateFinalPricePerDay(planDuration)).thenReturn(expectedTotalPrice);
        when(purchaseRepository.save(any(TrainingPlanPurchase.class))).thenReturn(purchase);

        //when
        CheckoutResponse result = purchaseService.purchaseCheckout(request);

        //then
        assertThat(result.getOrderStatus().name()).isEqualTo(purchase.getPurchaseStatus().name());
        assertThat(result.getFinalPrice()).isEqualTo(expectedTotalPrice);
    }

    @Test
    void shouldReturnPurchaseResponseWhenCompletePurchase() {
        //given
        final String paymentCode = paymentCode();
        final TrainingPlanPurchase purchase = trainingPlanPurchase();
        when(purchaseRepository.findByPaymentCode(paymentCode)).thenReturn(Optional.of(purchase));
        when(trainingPlanService.createTrainingPlan(any(), any())).thenReturn(trainingPlan());

        //when
        PurchaseResponse result = purchaseService.completePurchase(paymentCode);

        //then
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatusEnum.COMPLETED);
        verify(fileExportService, times(1)).sendExcelTrainingPlanToEmail(any());
    }

    @Test
    void shouldThrowPurchaseErrorExceptionWhenPlanGeneratingFailed() {
        //given
        final String paymentCode = paymentCode();
        final TrainingPlanPurchase purchase = trainingPlanPurchase();
        when(purchaseRepository.findByPaymentCode(paymentCode)).thenReturn(Optional.of(purchase));
        when(trainingPlanService.createTrainingPlan(any(), any()))
                .thenThrow(new TrainingPlanException(ExceptionType.NOT_ENOUGH_EXERCISES));

        //when && then
        assertThatThrownBy(() -> purchaseService.completePurchase(paymentCode))
                .isInstanceOf(PurchaseException.class)
                .hasMessage(PurchaseExceptionType.PURCHASE_ERROR.getMessage());
    }

    @Test
    void shouldThrowPurchaseInvalidExceptionWhenCompletePurchaseInvalid() {
        //given
        final String paymentCode = paymentCode();
        final TrainingPlanPurchase purchase = trainingPlanPurchase();
        purchase.markAsFailed();
        when(purchaseRepository.findByPaymentCode(paymentCode)).thenReturn(Optional.of(purchase));

        //when && then
        assertThatThrownBy(() -> purchaseService.completePurchase(paymentCode))
                .isInstanceOf(PurchaseException.class)
                .hasMessage(PurchaseExceptionType.PURCHASE_INVALID.getMessage());
    }

    @Test
    void shouldThrowPaymentRequiredExceptionWhenCompletePurchaseUnpaid() {
        //given
        final String paymentCode = paymentCode();
        when(purchaseRepository.findByPaymentCode(paymentCode)).thenReturn(Optional.empty());

        //when && then
        assertThatThrownBy(() -> purchaseService.completePurchase(paymentCode))
                .isInstanceOf(PurchaseException.class)
                .hasMessage(PurchaseExceptionType.PAYMENT_REQUIRED.getMessage());
    }

    @Test
    void shouldThrowPurchaseErrorExceptionWhenPurchaseCheckout() {
        //given
        final String username = username();
        mockAuthenticatedUser(username);
        when(userAccessService.getUserByUsername(username)).thenReturn(mock(User.class));
        final PurchaseRequest request = purchaseRequest();
        when(purchaseRepository.save(any(TrainingPlanPurchase.class))).thenThrow(
                new DataIntegrityViolationException("ex"));

        //when && then
        assertThatThrownBy(() -> purchaseService.purchaseCheckout(request))
                .isInstanceOf(PurchaseException.class)
                .hasMessage(PurchaseExceptionType.PURCHASE_ERROR.getMessage());
    }

    private void mockAuthenticatedUser(String username) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }
}
