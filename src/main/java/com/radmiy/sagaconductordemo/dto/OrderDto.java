package com.radmiy.sagaconductordemo.dto;

import com.radmiy.sagaconductordemo.repository.model.StepStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderDto(

        UUID id,
        UUID userId,
        StepStatus status,
        String note,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
