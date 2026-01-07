package com.radmiy.sagaconductordemo.conductor.workers;

import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.sdk.workflow.task.WorkerTask;
import com.radmiy.sagaconductordemo.repository.model.Payment;
import com.radmiy.sagaconductordemo.repository.model.StepStatus;
import com.radmiy.sagaconductordemo.service.PaymentSagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.radmiy.sagaconductordemo.util.RandomErrorUtil.getError;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentSagaWorkers {

    private final PaymentSagaService service;

    @WorkerTask("debit_payment")
    public TaskResult debitPayment(Map<String, Object> input) {
        log.info("Requested creation payment");
        var result = new TaskResult();
        var amount = new BigDecimal(input.get("amount").toString());
        var userId = UUID.fromString(input.get("userId").toString());
        var orderId = UUID.fromString(input.get("orderId").toString());

        result = getError(result,
                "Error in payment step",
                "Insufficient funds on balance");
        if (result.getStatus() == TaskResult.Status.FAILED_WITH_TERMINAL_ERROR) {
            return result;
        }

        var payment = Payment.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .orderId(orderId)
                .note("Create Payment for order %s".formatted(orderId))
                .status(StepStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .amount(amount)
                .build();
        try {
            payment = service.create(payment);
            log.info("Payment created");
        } catch (Exception err) {
            log.error("Error while saving payment", err);
            throw new RuntimeException(err);
        }

        Map<String, Object> output = Map.of(
                "paymentId", payment.getId()
        );
        result.setOutputData(output);
        result.setStatus(TaskResult.Status.COMPLETED);
        return result;
    }

    @WorkerTask("refund_payment")
    public void refundPayment(Map<String, Object> input) {
        log.info("Requested refund");
        Optional.ofNullable(input.get("paymentId"))
                .ifPresent(idObj -> {
                    var id = UUID.fromString(idObj.toString());
                    service.cancel(id);
                    log.info("Refunded");
                });
    }
}
