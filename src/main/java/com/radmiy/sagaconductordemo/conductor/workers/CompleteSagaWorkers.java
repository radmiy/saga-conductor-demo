package com.radmiy.sagaconductordemo.conductor.workers;

import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.sdk.workflow.task.WorkerTask;
import com.radmiy.sagaconductordemo.repository.model.Inventory;
import com.radmiy.sagaconductordemo.repository.model.Order;
import com.radmiy.sagaconductordemo.repository.model.Payment;
import com.radmiy.sagaconductordemo.repository.model.Shipment;
import com.radmiy.sagaconductordemo.service.SagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class CompleteSagaWorkers {

    private final SagaService<Order> orderSagaService;
    private final SagaService<Payment> paymentSagaService;
    private final SagaService<Inventory> inventorySagaService;
    private final SagaService<Shipment> shippingSagaService;

    @WorkerTask("complete_saga_task")
    public TaskResult completeSaga(Task task) {
        log.info("Requested task complete");
        var result = new TaskResult(task);

        var orderId = UUID.fromString(task.getInputData().get("orderId").toString());
        var paymentId = UUID.fromString(task.getInputData().get("paymentId").toString());
        var inventoryId = UUID.fromString(task.getInputData().get("inventoryId").toString());
        var shipmentId = UUID.fromString(task.getInputData().get("shipmentId").toString());

        try {
            orderSagaService.confirm(orderId);
            paymentSagaService.confirm(paymentId);
            inventorySagaService.confirm(inventoryId);
            shippingSagaService.confirm(shipmentId);
            log.info("Task completed");
        } catch (Exception err) {
            result.setReasonForIncompletion(err.getMessage());
        }

        result.setStatus(TaskResult.Status.COMPLETED);
        return result;
    }
}
