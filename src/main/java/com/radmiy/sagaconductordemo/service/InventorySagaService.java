package com.radmiy.sagaconductordemo.service;

import com.radmiy.sagaconductordemo.repository.InventorySagaRepository;
import com.radmiy.sagaconductordemo.repository.model.Inventory;
import com.radmiy.sagaconductordemo.repository.model.StepStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class InventorySagaService implements SagaService<Inventory> {

    private final InventorySagaRepository repository;

    @Override
    public Inventory create(Inventory inventory) {
        return repository.save(inventory);
    }

    @Override
    public void cancel(UUID id) {
        updateStatus(id, StepStatus.FAILED);
    }

    @Override
    public void updateStatus(UUID id, StepStatus status) {
        var inventory = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory with id=%s does not exist".formatted(id)));
        inventory.setStatus(status);
        inventory.setUpdatedAt(LocalDateTime.now());
        repository.save(inventory);
    }

    @Override
    public Optional<Inventory> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public void confirm(UUID id) {
        updateStatus(id, StepStatus.COMPLETED);
    }

    @Override
    public boolean isExist(UUID userId, UUID orderId) {
        return repository.existsByUserIdAndOrderId(userId, orderId);
    }
}
