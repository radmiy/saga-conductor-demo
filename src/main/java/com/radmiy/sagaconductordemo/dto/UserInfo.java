package com.radmiy.sagaconductordemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record UserInfo(

        UUID id,
        List<OrderDto> orders,
        List<PaymentDto> payments,
        List<InventoryDto> inventories,
        List<ShipmentDto> shipments
) {
}
