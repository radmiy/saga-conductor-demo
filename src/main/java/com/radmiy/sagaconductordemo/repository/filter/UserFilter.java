package com.radmiy.sagaconductordemo.repository.filter;

import com.radmiy.sagaconductordemo.repository.model.StepStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record UserFilter(

        UUID userId,
        StepStatus status,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        Instant createdAfter,
        Instant createdBefore,
        String item,
        String address
) {
}
