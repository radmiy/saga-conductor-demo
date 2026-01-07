package com.radmiy.sagaconductordemo.service;

import com.netflix.conductor.client.http.WorkflowClient;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.radmiy.sagaconductordemo.dto.OrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class StartWorkflowService {

    private final WorkflowClient workflowClient;

    public String startSaga(OrderRequest orderRequest) {
        log.info("Request to run Saga for a user: {}. Amount: {}",
                orderRequest.userId(), orderRequest.amount());

        StartWorkflowRequest request = getStartWorkflowRequest(orderRequest);

        try {
            String workflowId = workflowClient.startWorkflow(request);
            log.info("Saga launched successfully. Workflow ID: {}", workflowId);

            return workflowId;
        } catch (Exception e) {
            log.error("Critical error when starting Saga: {}", e.getMessage());
            throw e;
        }
    }

    private StartWorkflowRequest getStartWorkflowRequest(OrderRequest orderRequest) {
        StartWorkflowRequest request = new StartWorkflowRequest();
        request.setName("order_placement_saga");
        request.setVersion(1);

        Map<String, Object> input = Map.of(
                "userId", orderRequest.userId().toString(),
                "amount", orderRequest.amount(),
                "items", orderRequest.items(),
                "address", orderRequest.address()
        );

        request.setInput(input);

        request.setCorrelationId("order-saga-" + orderRequest.userId());
        return request;
    }
}
