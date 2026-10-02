package com.platform.backend.repository;

import com.platform.backend.entity.BioBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BioBlockRepository extends JpaRepository<BioBlock, UUID> {
    List<BioBlock> findByProfileIdOrderBySortOrderAsc(UUID profileId);
    List<BioBlock> findByProfileIdAndIsEnabledTrueOrderBySortOrderAsc(UUID profileId);
}