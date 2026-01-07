package com.radmiy.sagaconductordemo.repository.filter;

import com.radmiy.sagaconductordemo.repository.model.Inventory;
import com.radmiy.sagaconductordemo.repository.model.Order;
import com.radmiy.sagaconductordemo.repository.model.Payment;
import com.radmiy.sagaconductordemo.repository.model.Shipment;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderSpecifications {

    public static Specification<Order> build(UserFilter filter) {
        Specification<Order> mainSpec =
                Specification.unrestricted();

        // 1. Filter by userId
        if (filter.userId() != null) {
            mainSpec = mainSpec.and((root, query, cb) ->
                    cb.equal(root.get("userId"), filter.userId()));
        }
        // 2. Filter by status
        if (filter.status() != null) {
            mainSpec = mainSpec.and((root, query, cb) ->
                    cb.equal(root.get("status"), filter.status()));
        }
        // 3. Filter by createdAt
        if (filter.createdAfter() != null) {
            mainSpec = mainSpec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("createdAt"),
                            filter.createdAfter()));
        }
        if (filter.createdBefore() != null) {
            mainSpec = mainSpec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("createdAt"),
                            filter.createdBefore()));
        }

        // 4. Filter by Amount
        if (filter.minAmount() != null || filter.maxAmount() != null) {
            mainSpec = mainSpec.and((root, query, cb) -> {
                Subquery<UUID> subquery = query.subquery(UUID.class);
                Root<Payment> paymentRoot = subquery.from(Payment.class);
                subquery.select(paymentRoot.get("orderId"));

                List<Predicate> subPredicates = new ArrayList<>();
                if (filter.minAmount() != null)
                    subPredicates.add(
                            cb.greaterThanOrEqualTo(paymentRoot.get("amount"),
                                    filter.minAmount())
                    );
                if (filter.maxAmount() != null)
                    subPredicates.add(
                            cb.lessThanOrEqualTo(paymentRoot.get("amount"),
                                    filter.maxAmount())
                    );

                return root.get("id").in(subquery.where(
                        cb.and(subPredicates.toArray(new Predicate[0]))
                ));
            });
        }

        // 5. Filter by Inventory
        if (filter.item() != null) {
            mainSpec = mainSpec.and((root, query, cb) -> {
                Subquery<UUID> subquery = query.subquery(UUID.class);
                Root<Inventory> invRoot = subquery.from(Inventory.class);
                subquery.select(invRoot.get("orderId"));

                String jsonItem = "\"" + filter.item() + "\"";
                Predicate containsPredicate = cb.isTrue(
                        cb.function("jsonb_contains", Boolean.class,
                                invRoot.get("items"), cb.literal(jsonItem))
                );

                return root.get("id").in(subquery.where(containsPredicate));
            });
        }

        // 6. Filter by Address
        if (filter.address() != null) {
            mainSpec = mainSpec.and((root, query, cb) -> {
                Subquery<UUID> subquery = query.subquery(UUID.class);
                Root<Shipment> shipRoot = subquery.from(Shipment.class);
                subquery.select(shipRoot.get("orderId"));
                subquery.where(cb.equal(shipRoot.get("address"), filter.address()));

                return root.get("id").in(subquery);
            });
        }

        return mainSpec;
    }
}
