package com.radmiy.sagaconductordemo.repository;

import com.radmiy.sagaconductordemo.repository.model.Inventory;
import com.radmiy.sagaconductordemo.repository.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventorySagaRepository extends JpaRepository<Inventory, UUID>, JpaSpecificationExecutor<Order> {

    List<Inventory> findByOrderIdIn(List<UUID> orderIds);

    @Query("SELECT count(i) > 0 FROM Inventory i WHERE i.userId = :userId AND i.orderId = :orderId")
    boolean existsByUserIdAndOrderId(@Param("userId") UUID userId, @Param("orderId") UUID orderId);
}
