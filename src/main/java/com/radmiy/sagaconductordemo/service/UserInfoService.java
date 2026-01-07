package com.radmiy.sagaconductordemo.service;

import com.radmiy.sagaconductordemo.dto.UserInfo;
import com.radmiy.sagaconductordemo.mapper.UserInfoMapper;
import com.radmiy.sagaconductordemo.repository.InventorySagaRepository;
import com.radmiy.sagaconductordemo.repository.OrderSagaRepository;
import com.radmiy.sagaconductordemo.repository.PaymentSagaRepository;
import com.radmiy.sagaconductordemo.repository.ShippingSagaRepository;
import com.radmiy.sagaconductordemo.repository.filter.OrderSpecifications;
import com.radmiy.sagaconductordemo.repository.filter.UserFilter;
import com.radmiy.sagaconductordemo.repository.model.Inventory;
import com.radmiy.sagaconductordemo.repository.model.Order;
import com.radmiy.sagaconductordemo.repository.model.Payment;
import com.radmiy.sagaconductordemo.repository.model.Shipment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserInfoService {

    private final UserInfoMapper mapper;

    private final OrderSagaRepository orderRepository;
    private final PaymentSagaRepository paymentRepository;
    private final InventorySagaRepository inventoryRepository;
    private final ShippingSagaRepository shipmentRepository;

    @Transactional(readOnly = true)
    public Page<UserInfo> search(UserFilter filter, Pageable pageable) {
        var orderPage = orderRepository.findAll(OrderSpecifications.build(filter), pageable);

        if (orderPage.isEmpty()) {
            return Page.empty();
        }

        List<UUID> orderIds = orderPage.getContent().stream()
                .map(Order::getId)
                .toList();

        List<Payment> allPayments = paymentRepository.findByOrderIdIn(orderIds);
        List<Inventory> allInventories = inventoryRepository.findByOrderIdIn(orderIds);
        List<Shipment> allShipments = shipmentRepository.findByOrderIdIn(orderIds);

        var paymentsMap = allPayments.stream().collect(groupingBy(Payment::getOrderId));
        var inventoryMap = allInventories.stream().collect(groupingBy(Inventory::getOrderId));
        var shipmentMap = allShipments.stream().collect(groupingBy(Shipment::getOrderId));

        return orderPage.map(order -> {
            UUID oId = order.getId();

            return new UserInfo(
                    order.getUserId(),
                    mapper.toOrderDtoList(List.of(order)),
                    mapper.toPaymentDtoList(paymentsMap.getOrDefault(oId, List.of())),
                    mapper.toInventoryDtoList(inventoryMap.getOrDefault(oId, List.of())),
                    mapper.toShipmentDtoList(shipmentMap.getOrDefault(oId, List.of()))
            );
        });
    }
}