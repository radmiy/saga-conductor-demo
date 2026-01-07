package com.radmiy.sagaconductordemo.mapper;

import com.radmiy.sagaconductordemo.dto.InventoryDto;
import com.radmiy.sagaconductordemo.dto.OrderDto;
import com.radmiy.sagaconductordemo.dto.PaymentDto;
import com.radmiy.sagaconductordemo.dto.ShipmentDto;
import com.radmiy.sagaconductordemo.dto.UserInfo;
import com.radmiy.sagaconductordemo.repository.model.Inventory;
import com.radmiy.sagaconductordemo.repository.model.Order;
import com.radmiy.sagaconductordemo.repository.model.Payment;
import com.radmiy.sagaconductordemo.repository.model.Shipment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserInfoMapper {

    OrderDto toOrderDto(Order order);

    PaymentDto toPaymentDto(Payment payment);

    InventoryDto toInventoryDto(Inventory inventory);

    ShipmentDto toShipmentDto(Shipment shipment);

    // Маппинг списков (MapStruct сам применит методы выше для каждого элемента)
    List<OrderDto> toOrderDtoList(List<Order> orders);

    List<PaymentDto> toPaymentDtoList(List<Payment> payments);

    List<InventoryDto> toInventoryDtoList(List<Inventory> inventories);

    List<ShipmentDto> toShipmentDtoList(List<Shipment> shipments);
}

