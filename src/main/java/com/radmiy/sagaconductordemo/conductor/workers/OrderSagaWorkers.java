package com.radmiy.sagaconductordemo.conductor.workers;

import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.sdk.workflow.task.WorkerTask;
import com.radmiy.sagaconductordemo.repository.model.Order;
import com.radmiy.sagaconductordemo.repository.model.StepStatus;
import com.radmiy.sagaconductordemo.service.OrderSagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.radmiy.sagaconductordemo.util.RandomErrorUtil.getError;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderSagaWorkers {

    private final OrderSagaService service;

    /**
     * Order Creation Task
     *
     * @param input data that contains the userId
     * @return output data that contains orderId
     */
    @WorkerTask("reserve_order")
    public TaskResult createOrder(Map<String, Object> input) {
        log.info("Requested creation order");
        var result = new TaskResult();
        var userId = UUID.fromString(input.get("userId").toString());
        String step = Optional.ofNullable(input.get("step"))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .orElse(null);

        try {
            var orderId = UUID.randomUUID();
            if (!service.isExist(userId, orderId)) {
                // flow error simulation
                String error = null;
                if ("order".equalsIgnoreCase(step)) {
                    error = input.get("error").toString();
                }
                result = getError(result,
                        error,
                        "Error in order step",
                        "Cannot create order");
                if (result.getStatus() == TaskResult.Status.FAILED_WITH_TERMINAL_ERROR) {
                    return result;
                }

                var order = Order.builder()
                        .id(orderId)
                        .userId(userId)
                        .note("Create Order for user %s".formatted(userId))
                        .status(StepStatus.PENDING)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                order = service.create(order);
                log.info("Order created");

                Map<String, Object> output = Map.of(
                        "orderId", order.getId()
                );
                result.setOutputData(output);
                result.setStatus(TaskResult.Status.COMPLETED);
            } else {
                log.error("Error: order with id:{} exist.", orderId);
                result.setStatus(TaskResult.Status.FAILED);
            }
            return result;
        } catch (Exception err) {
            log.error("Error while creating order", err);
            result.setStatus(TaskResult.Status.FAILED);
            return result;
        }
    }

    /**
     * Compensation task
     *
     * @param input data that contains orderId
     */
    @WorkerTask("cancel_order")
    public void cancelOrder(Map<String, Object> input) {
        log.info("Requested cancel order");
        Optional.ofNullable(input.get("orderId"))
                .ifPresent(idObj -> {
                    var id = UUID.fromString(idObj.toString());
                    service.cancel(id);
                    log.info("Canceled order");
                });
    }
}
