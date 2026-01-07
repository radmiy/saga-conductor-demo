package com.radmiy.sagaconductordemo.service;

import com.radmiy.sagaconductordemo.dto.UserInfo;
import com.radmiy.sagaconductordemo.mapper.UserInfoMapper;
import com.radmiy.sagaconductordemo.repository.OrderSagaRepository;
import com.radmiy.sagaconductordemo.repository.filter.OrderSpecifications;
import com.radmiy.sagaconductordemo.repository.filter.UserFilter;
import com.radmiy.sagaconductordemo.repository.model.Order;
import com.radmiy.sagaconductordemo.repository.model.StepStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderSagaService implements SagaService<Order> {

    private final UserInfoMapper userInfoMapper;
    private final OrderSagaRepository repository;

    @Override
    public Order create(Order order) {
        return repository.save(order);
    }

    @Override
    public void cancel(UUID id) {
        updateStatus(id, StepStatus.FAILED);
    }

    @Override
    public void updateStatus(UUID id, StepStatus status) {
        var order = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order with id=%s does not exist".formatted(id)));
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        repository.save(order);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public void confirm(UUID id) {
        updateStatus(id, StepStatus.COMPLETE);
    }
}
