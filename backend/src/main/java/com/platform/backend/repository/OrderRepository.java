package com.platform.backend.repository;

import com.platform.backend.entity.Order;
import com.platform.backend.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByCreatorIdOrderByCreatedAtDesc(UUID creatorId);
    List<Order> findByBuyerEmailOrderByCreatedAtDesc(String buyerEmail);
    Optional<Order> findByPaymentRef(String paymentRef);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o where o.creator.id = :creatorId and o.status = :status") BigDecimal sumTotalRevenueByCreatorIdAndStatus(
        @Param("creatorId") UUID creatorId,
        @Param("status") OrderStatus status
    );
}