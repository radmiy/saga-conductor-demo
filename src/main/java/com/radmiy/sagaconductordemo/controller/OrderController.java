package com.radmiy.sagaconductordemo.controller;

import com.radmiy.sagaconductordemo.dto.OrderRequest;
import com.radmiy.sagaconductordemo.dto.UserInfo;
import com.radmiy.sagaconductordemo.repository.filter.UserFilter;
import com.radmiy.sagaconductordemo.service.SagaWorkflowService;
import com.radmiy.sagaconductordemo.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final SagaWorkflowService service;
    private final UserInfoService userInfoService;

    /**
     * Method for launching the main workflow
     *
     * @param orderRequest input data that contains all the parameters necessary for launching
     * @return
     */
    @PostMapping("/start-saga")
    public ResponseEntity<String> startSaga(@RequestBody OrderRequest orderRequest) {
        log.info("Request to run Saga for a user: {}. Amount: {}",
                orderRequest.userId(), orderRequest.amount());

        try {
            String workflowId = service.startSaga(orderRequest);
            return ResponseEntity.ok("Saga has launched. Workflow ID: " + workflowId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Startup error: " + e.getMessage());
        }
    }

    @GetMapping("/status/{workflowId}")
    public ResponseEntity<?> getStatus(@PathVariable String workflowId) {
        try {
            var workflow = service.getWorkflowStatus(workflowId);

            String formattedStartTime = getFormattedTime(workflow.getStartTime());
            String formattedEndTime = getFormattedTime(workflow.getEndTime());

            return ResponseEntity.ok(Map.of(
                    "workflowId", workflow.getWorkflowId(),
                    "status", workflow.getStatus(), // RUNNING, COMPLETED, FAILED, TERMINATED
                    "startTime", formattedStartTime,
                    "endTime", formattedEndTime,
                    "reason", workflow.getReasonForIncompletion() != null ?
                            workflow.getReasonForIncompletion() :
                            "None",
                    "tasks", workflow.getTasks().stream()
                            .map(t -> Map.of(
                                    "taskName", t.getTaskDefName(),
                                    "status", t.getStatus()
                            )).toList()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Workflow not found: %s".formatted(e.getMessage()));
        }
    }

    private static String getFormattedTime(long startTimeLong) {
        String formattedStartTime = (startTimeLong > 0)
                ? Instant.ofEpochMilli(startTimeLong).toString()
                : "In progress";
        return formattedStartTime;
    }

    /**
     * Method of searching for orders with filtering, paging and sorting
     *
     * @param filter   input data by which filtering is performed
     * @param pageable input data by which paging and sorting is performed
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity<Page<UserInfo>> getPaymentsByStatus(
            @ModelAttribute UserFilter filter,
            @PageableDefault(page = 0, size = 25, sort = "amount", direction = DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok().body(userInfoService.search(filter, pageable));
    }
}
