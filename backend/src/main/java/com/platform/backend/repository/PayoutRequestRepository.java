package com.platform.backend.repository;

import com.platform.backend.entity.PayoutRequest;
import com.platform.backend.entity.PayoutStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PayoutRequestRepository extends JpaRepository<PayoutRequest, UUID> {
    List<PayoutRequest> findByCreatorIdOrderByRequestedAtDesc(UUID creatorId);
    List<PayoutRequest> findByStatusOrderByRequestedAtDesc(PayoutStatus status);
}