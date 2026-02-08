package org.lukawska.trainsmart.purchase.application.service;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.fileexport.application.service.FileExportService;
import org.lukawska.trainsmart.purchase.application.exception.PurchaseException;
import org.lukawska.trainsmart.purchase.application.exception.PurchaseExceptionType;
import org.lukawska.trainsmart.purchase.domain.entity.TrainingPlanPurchase;
import org.lukawska.trainsmart.purchase.domain.repository.TrainingPlanPurchaseRepository;
import org.lukawska.trainsmart.purchase.domain.valueObject.PurchaseStatus;
import org.lukawska.trainsmart.purchase.model.*;
import org.lukawska.trainsmart.trainingplan.application.exception.ExceptionType;
import org.lukawska.trainsmart.trainingplan.application.exception.TrainingPlanException;
import org.lukawska.trainsmart.trainingplan.application.service.TrainingPlanService;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import({PostgresTestConfig.class, TestFixtures.class})
class TrainingPlanPurchaseServiceIT {

    @Autowired
    private TrainingPlanPurchaseService purchaseService;

    @Autowired
    private TrainingPlanPurchaseRepository purchaseRepository;

    @Autowired
    private TestFixtures testFixtures;

    @MockitoBean
    private FileExportService fileExportService;

    @MockitoBean
    private TrainingPlanService trainingPlanService;

    @WithMockUser(username = "active_user")
    @Test
    void shouldSavePurchaseAndReturnCheckoutResponseWhenPurchaseCheckout() {
        //given
        testFixtures.user().withUsername("active_user").save();
        final PurchaseRequest request = PurchaseRequest.builder()
                                                       .trainingType(TrainingTypeEnum.STRENGTH)
                                                       .planDuration(PlanDurationEnum.FOUR_WEEKS)
                                                       .daysPerWeek(2)
                                                       .preferredDays(List.of(WeekDaysEnum.MONDAY, WeekDaysEnum.FRIDAY))
                                                       .build();

        //when
        CheckoutResponse checkoutResponse = purchaseService.purchaseCheckout(request);

        //then
        Optional<TrainingPlanPurchase> savedPurchase = purchaseRepository.findById(checkoutResponse.getPurchaseId());
        assertThat(savedPurchase.isPresent()).isTrue();
        TrainingPlanPurchase purchase = savedPurchase.get();
        assertThat(purchase.getPlanDuration().name()).isEqualTo(request.getPlanDuration().name());
        assertThat(purchase.getTrainingType().name()).isEqualTo(request.getTrainingType().name());
        assertThat(purchase.getDaysPerWeek()).isEqualTo(request.getDaysPerWeek());
        assertThat(purchase.getPurchaseStatus()).isEqualTo(PurchaseStatus.PENDING);
    }

    @Test
    void shouldReturnPurchaseResponseWhenCompletePurchaseSuccess() {
        //given
        final TrainingPlanPurchase planPurchase = testFixtures.trainingPlanPurchase().save();
        final String paymentCode = planPurchase.getPaymentCode();
        when(trainingPlanService.createTrainingPlan(any(), any())).thenReturn(mock(TrainingPlan.class));

        //when
        PurchaseResponse purchaseResponse = purchaseService.completePurchase(paymentCode);

        //then
        assertThat(purchaseResponse.getOrderStatus().name()).isEqualTo(planPurchase.getPurchaseStatus().name());
        assertThat(purchaseResponse.getFinalPrice()).isEqualTo(planPurchase.getTotalPrice());
        assertThat(planPurchase.getTrainingPlan()).isNotNull();
        assertThat(planPurchase.getPurchaseStatus()).isEqualTo(PurchaseStatus.COMPLETED);
    }

    @Test
    void shouldMarkPurchaseAsFailedAndCommitWhenTrainingPlanException() {
        //given
        final TrainingPlanPurchase planPurchase = testFixtures.trainingPlanPurchase().save();
        when(trainingPlanService.createTrainingPlan(any(), any()))
                .thenThrow(new TrainingPlanException(ExceptionType.NOT_ENOUGH_EXERCISES));

        //when && then
        assertThatThrownBy(() -> purchaseService.completePurchase(planPurchase.getPaymentCode()))
                .isInstanceOf(PurchaseException.class)
                .hasMessage(PurchaseExceptionType.PURCHASE_ERROR.getMessage());
        TrainingPlanPurchase reloaded = purchaseRepository.findById(planPurchase.getId()).orElseThrow();
        assertThat(reloaded.getPurchaseStatus()).isEqualTo(PurchaseStatus.FAILED);
        assertThat(reloaded.getTrainingPlan()).isNull();
    }
}
