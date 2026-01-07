package com.radmiy.sagaconductordemo.repository;

import com.radmiy.sagaconductordemo.repository.model.Inventory;
import com.radmiy.sagaconductordemo.repository.model.Order;
import com.radmiy.sagaconductordemo.repository.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventorySagaRepository extends JpaRepository<Inventory, UUID>, JpaSpecificationExecutor<Order> {

    List<Inventory> findByOrderIdIn(List<UUID> orderIds);
}
