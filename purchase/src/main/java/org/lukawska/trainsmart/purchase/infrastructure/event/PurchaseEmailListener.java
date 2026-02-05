package org.lukawska.trainsmart.purchase.infrastructure.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.fileexport.application.dto.EmailExcelExportCommand;
import org.lukawska.trainsmart.fileexport.application.service.FileExportService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class PurchaseEmailListener {

    private final FileExportService fileExportService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePurchaseCompleted(PurchaseCompletedEvent event) {
        log.info("Received PurchaseCompletedEvent for Plan ID: {} and User ID: {}", event.planId(), event.userId());
        EmailExcelExportCommand command = new EmailExcelExportCommand(event.planId(), event.userId(), event.email());
        fileExportService.sendExcelTrainingPlanToEmail(command);
    }
}
