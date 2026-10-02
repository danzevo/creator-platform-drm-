package com.platform.backend.repository;

import com.platform.backend.entity.DigitalProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DigitalProductRepository extends JpaRepository<DigitalProduct, UUID> {
    List<DigitalProduct> findByCreatorIdOrderByCreatedAtDesc(UUID creatorId);
    List<DigitalProduct> findByCreatorIdAndIsPublishedTrueOrderByCreatedAtDesc(UUID creatorId);
}