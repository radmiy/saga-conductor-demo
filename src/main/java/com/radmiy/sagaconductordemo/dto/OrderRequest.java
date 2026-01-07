package com.radmiy.sagaconductordemo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderRequest(
        @NotNull(message = "userId cannot be null")
        UUID userId,

        @NotNull(message = "Amount required")
        @Positive(message = "The amount must be greater than zero")
        BigDecimal amount,

        @NotEmpty(message = "The product list cannot be empty")
        List<String> items,

        @NotBlank(message = "Delivery address is required")
        String address
) {
}
