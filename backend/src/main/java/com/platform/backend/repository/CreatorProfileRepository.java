package com.platform.backend.repository;

import com.platform.backend.entity.CreatorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreatorProfileRepository extends JpaRepository<CreatorProfile, UUID> {
    Optional<CreatorProfile> findByUsername(String username);
    Optional<CreatorProfile> findByUserId(UUID userId);
    boolean existsByUsername(String username);
}