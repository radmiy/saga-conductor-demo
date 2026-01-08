package com.radmiy.sagaconductordemo.service;

import com.radmiy.sagaconductordemo.repository.PaymentSagaRepository;
import com.radmiy.sagaconductordemo.repository.model.Payment;
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
public class PaymentSagaService implements SagaService<Payment> {

    private final PaymentSagaRepository repository;

    @Override
    public Payment create(Payment payment) {
        return repository.save(payment);
    }

    @Override
    public void cancel(UUID id) {
        updateStatus(id, StepStatus.FAILED);
    }

    @Override
    public void updateStatus(UUID id, StepStatus status) {
        checkId(id);
        final Payment payment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment with id=%s does not exist".formatted(id)));
        payment.setStatus(status);
        payment.setUpdatedAt(LocalDateTime.now());
        repository.save(payment);
    }

    @Override
    public Optional<Payment> findById(UUID id) {
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

    private void checkId(UUID id) {
        if (id == null) {
            throw new RuntimeException("Id cannot be null");
        } else if (!repository.existsById(id)) {
            throw new RuntimeException("Payment with id=%s does not exist".formatted(id));
        }
    }
}
