package com.radmiy.sagaconductordemo.repository;

import com.radmiy.sagaconductordemo.repository.model.Order;
import com.radmiy.sagaconductordemo.repository.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShippingSagaRepository extends JpaRepository<Shipment, UUID>, JpaSpecificationExecutor<Order> {

    List<Shipment> findByOrderIdIn(List<UUID> orderIds);

    @Query("SELECT count(s) > 0 FROM Shipment s WHERE s.userId = :userId AND s.id = :orderId")
    boolean existsByUserIdAndOrderId(@Param("userId") UUID userId, @Param("orderId") UUID orderId);
}
