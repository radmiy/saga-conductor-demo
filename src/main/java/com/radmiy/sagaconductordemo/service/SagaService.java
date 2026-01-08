package com.radmiy.sagaconductordemo.service;

import com.radmiy.sagaconductordemo.repository.model.StepStatus;

import java.util.Optional;
import java.util.UUID;

/**
 * Service layer for managing order, payment, inventory and shipment.
 * Provides business operations for retrieving, creating and deleting objects.
 */
public interface SagaService<T> {

    /**
     * Creates a new object
     * @param t the object data to create
     * @return the created object including generated identifiers or metadata
     */
    T create(T t);

    /**
     *
     * @param id
     */
    void cancel(UUID id);

    /**
     * Updates a object status by its identifier.
     * @param id the unique identifier of the object
     * @param status the object status
     */
    void updateStatus(UUID id, StepStatus status);

    /**
     * Returns a object by its identifier.
     * @param id the unique identifier of the object
     * @return the object, or {@code null} if the object does not exist
     */
    Optional<T> findById(UUID id);

    /**
     * Updates a object status to COMPLETED by its identifier.
     * @param orderId the unique identifier of the object
     */
    void confirm(UUID orderId);

    /**
     * Check is object exist by his parameters
     * @param userId
     * @param orderId
     * @return result of checking
     */
    boolean isExist(UUID userId, UUID orderId);
}
