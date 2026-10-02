package com.platform.backend.service;

import com.platform.backend.dto.BioBlockDtos.*;
import com.platform.backend.entity.*;
import com.platform.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BioBlockService {
    private final BioBlockRepository bioBlockRepository;
    private final CreatorProfileRepository profileRepository;

    @Transactional(readOnly = true)
    public List<BioBlockResponse> getBlocksForCreator(User user) {
        CreatorProfile profile = profileRepository.findByUserId(user.getId())
                                                .orElseThrow(() -> new IllegalArgumentException("Creator profile not found"));
        
        return bioBlockRepository.findByProfileIdOrderBySortOrderAsc(profile.getId()).stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
    }

    @Transactional
    public BioBlockResponse createBlock(User user, CreateBioBlockRequest req) {
        CreatorProfile profile = profileRepository.findByUserId(user.getId())
                                                .orElseThrow(() -> new IllegalArgumentException("Creator profile not found"));

        int order = (req.getSortOrder() != null) ? req.getSortOrder() : bioBlockRepository.findByProfileIdOrderBySortOrderAsc(profile.getId()).size();
        BioBlock block = BioBlock.builder()
                                .profile(profile)
                                .title(req.getTitle())
                                .url(req.getUrl())
                                .iconUrl(req.getIconUrl())
                                .blockType(req.getBlockType() != null ? req.getBlockType() : BlockType.LINK)
                                                                                                .sortOrder(order)
                                                                                                .isEnabled(true)
                                                                                                .build();

        return toResponse(bioBlockRepository.save(block));
    }

    @Transactional
    public BioBlockResponse updateBlock(User user, UUID blockId, UpdateBioBlockRequest req) {
        BioBlock block = bioBlockRepository.findById(blockId)
                                            .orElseThrow(() -> new IllegalArgumentException("Block not found"));

        if (!block.getProfile().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied to this block");
        }

        if (req.getTitle() != null)
            block.setTitle(req.getTitle());
        if (req.getUrl() != null)
            block.setUrl(req.getUrl());
        if (req.getIconUrl() != null) 
            block.setIconUrl(req.getIconUrl());
        if (req.getBlockType() != null)
            block.setBlockType(req.getBlockType());
        if (req.getSortOrder() != null)
            block.setSortOrder(req.getSortOrder());
        if (req.getIsEnabled() != null)
            block.setIsEnabled(req.getIsEnabled());

        return toResponse(bioBlockRepository.save(block));
    }

    @Transactional
    public void deleteBlock(User user, UUID blockId) {
        BioBlock block = bioBlockRepository.findById(blockId)
                                            .orElseThrow(() -> new IllegalArgumentException("Block not found"));
        
        if (!block.getProfile().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied to this block");
        }

        bioBlockRepository.delete(block);
    }

    @Transactional
    public void reorderBlocks(User user, ReorderBlocksRequest req) {
        CreatorProfile profile = profileRepository.findByUserId(user.getId())
                                                .orElseThrow(() -> new IllegalArgumentException("Creator profile not found"));

        for (ReorderItem item : req.getItems()) {
            bioBlockRepository.findById(item.getId()).ifPresent(b -> {
                if (b.getProfile().getId().equals(profile.getId())) {
                    b.setSortOrder(item.getSortOrder());

                    bioBlockRepository.save(b);
                }
            });
        }
    }

    private BioBlockResponse toResponse(BioBlock b) {
        return BioBlockResponse.builder()
                .id(b.getId())
                .title(b.getTitle())
                .url(b.getUrl())
                .iconUrl(b.getIconUrl())
                .blockType(b.getBlockType())
                .sortOrder(b.getSortOrder())
                .isEnabled(b.getIsEnabled())
                .build();
    }
}