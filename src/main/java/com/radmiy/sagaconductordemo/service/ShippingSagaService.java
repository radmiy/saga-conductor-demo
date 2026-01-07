package com.radmiy.sagaconductordemo.service;

import com.radmiy.sagaconductordemo.repository.ShippingSagaRepository;
import com.radmiy.sagaconductordemo.repository.model.Shipment;
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
public class ShippingSagaService implements SagaService<Shipment> {

    private final ShippingSagaRepository repository;

    @Override
    public Shipment create(Shipment shipment) {
        return repository.save(shipment);
    }

    @Override
    public void cancel(UUID id) {
        updateStatus(id, StepStatus.FAILED);
    }

    @Override
    public void updateStatus(UUID id, StepStatus status) {
        var shipment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipment with id=%s does not exist".formatted(id)));
        shipment.setStatus(status);
        shipment.setUpdatedAt(LocalDateTime.now());
        repository.save(shipment);
    }

    @Override
    public Optional<Shipment> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public void confirm(UUID id) {
        updateStatus(id, StepStatus.COMPLETE);
    }
}
