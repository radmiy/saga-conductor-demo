package com.radmiy.sagaconductordemo.service;

import com.radmiy.sagaconductordemo.repository.model.StepStatus;

import java.util.Optional;
import java.util.UUID;

public interface SagaService<T> {

    T create(T t);

    void cancel(UUID id);

    void updateStatus(UUID id, StepStatus status);

    Optional<T> findById(UUID id);

    void confirm(UUID orderId);
}
