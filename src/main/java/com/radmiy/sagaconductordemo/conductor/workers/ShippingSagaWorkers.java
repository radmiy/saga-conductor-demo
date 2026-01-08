package com.radmiy.sagaconductordemo.conductor.workers;

import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.sdk.workflow.task.WorkerTask;
import com.radmiy.sagaconductordemo.repository.model.Shipment;
import com.radmiy.sagaconductordemo.repository.model.StepStatus;
import com.radmiy.sagaconductordemo.service.ShippingSagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.radmiy.sagaconductordemo.util.RandomErrorUtil.getError;

@Slf4j
@RequiredArgsConstructor
@Component
public class ShippingSagaWorkers {

    private final ShippingSagaService service;

    /**
     * Shipping order creation task
     * @param input data that contains userId, orderId and address
     * @return output data that contains shipmentId
     */
    @WorkerTask("create_shipping")
    public TaskResult createShipping(Map<String, Object> input) {
        log.info("Requested creation shipment");
        var result = new TaskResult();
        var address = input.get("address").toString();
        var userId = UUID.fromString(input.get("userId").toString());
        var orderId = UUID.fromString(input.get("orderId").toString());

        try {
            if (!service.isExist(userId, orderId)) {
                // flow error simulation
                result = getError(result,
                        "Error in shipping step",
                        "Shipping address not exist");
                if (result.getStatus() == TaskResult.Status.FAILED_WITH_TERMINAL_ERROR) {
                    return result;
                }

                var shipment = Shipment.builder()
                        .id(UUID.randomUUID())
                        .userId(userId)
                        .orderId(orderId)
                        .note("Create Payment for order %s".formatted(orderId))
                        .status(StepStatus.PENDING)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .address(address)
                        .build();
                shipment = service.create(shipment);
                log.info("Shipment created");

                Map<String, Object> output = Map.of(
                        "shipmentId", shipment.getId()
                );
                result.setOutputData(output);
            }

            result.setStatus(TaskResult.Status.COMPLETED);
            return result;
        } catch (Exception err) {
            log.error("Error while saving shipping", err);
            result.setStatus(TaskResult.Status.FAILED);
            throw new RuntimeException(err);
        }
    }

    /**
     * Compensation task
     * @param input data that contains shipmentId
     */
    @WorkerTask("cancel_shipping")
    public void cancelShipping(Map<String, Object> input) {
        log.info("Requested cancel shipment");
        Optional.ofNullable(input.get("shipmentId"))
                .ifPresent(idObj -> {
                    var id = UUID.fromString(idObj.toString());
                    service.cancel(id);
                    log.info("Canceled shipment");
                });
    }
}
