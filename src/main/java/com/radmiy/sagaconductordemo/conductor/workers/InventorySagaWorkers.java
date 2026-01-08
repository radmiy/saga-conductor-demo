package com.radmiy.sagaconductordemo.conductor.workers;

import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.sdk.workflow.task.WorkerTask;
import com.radmiy.sagaconductordemo.repository.model.Inventory;
import com.radmiy.sagaconductordemo.repository.model.StepStatus;
import com.radmiy.sagaconductordemo.service.InventorySagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.radmiy.sagaconductordemo.util.RandomErrorUtil.getError;

@Slf4j
@RequiredArgsConstructor
@Component
public class InventorySagaWorkers {

    private final InventorySagaService service;

    /**
     * Inventory creation task
     * @param input data that contains userId, orderId and goods
     * @return outpot data that contains inventoryId
     */
    @WorkerTask("reserve_inventory")
    public TaskResult reserveInventory(Map<String, Object> input) {
        log.info("Requested reserving inventory");
        var result = new TaskResult();
        var userId = UUID.fromString(input.get("userId").toString());
        var orderId = UUID.fromString(input.get("orderId").toString());
        var items = (List<String>) input.get("items");

        try {
            if (!service.isExist(userId, orderId)) {
                // flow error simulation
                result = getError(result,
                        "Error in inventory step",
                        "Not enough goods in stock");
                if (result.getStatus() == TaskResult.Status.FAILED_WITH_TERMINAL_ERROR) {
                    return result;
                }

                var inventory = Inventory.builder()
                        .id(UUID.randomUUID())
                        .userId(userId)
                        .orderId(orderId)
                        .note("Create Payment for order %s".formatted(orderId))
                        .status(StepStatus.PENDING)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .items(items)
                        .build();
                inventory = service.create(inventory);
                log.info("Reserved inventory");

                Map<String, Object> output = Map.of(
                        "inventoryId", inventory.getId()
                );
                result.setOutputData(output);
            }

            result.setStatus(TaskResult.Status.COMPLETED);
            return result;
        } catch (Exception err) {
            log.error("Error while saving inventory", err);
            result.setStatus(TaskResult.Status.FAILED);
            throw new RuntimeException(err);
        }


    }

    /**
     * Compensation task
     * @param input data that contains inventoryId
     */
    @WorkerTask("release_inventory")
    public void compensateStep1(Map<String, Object> input) {
        log.info("Requested release inventory");
        Optional.ofNullable(input.get("inventoryId"))
                .ifPresent(idObj -> {
                    var id = UUID.fromString(idObj.toString());
                    service.cancel(id);
                    log.info("Released inventory");
                });
    }
}
