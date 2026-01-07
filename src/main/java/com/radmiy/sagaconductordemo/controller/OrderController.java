package com.radmiy.sagaconductordemo.controller;

import com.radmiy.sagaconductordemo.dto.OrderRequest;
import com.radmiy.sagaconductordemo.dto.UserInfo;
import com.radmiy.sagaconductordemo.repository.filter.UserFilter;
import com.radmiy.sagaconductordemo.service.StartWorkflowService;
import com.radmiy.sagaconductordemo.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final StartWorkflowService service;
    private final UserInfoService userInfoService;

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

    @GetMapping("/search")
    public ResponseEntity<Page<UserInfo>> getPaymentsByStatus(
            @ModelAttribute UserFilter filter,
            @PageableDefault(page = 0, size = 25, sort = "amount", direction = DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok().body(userInfoService.search(filter, pageable));
    }
}
